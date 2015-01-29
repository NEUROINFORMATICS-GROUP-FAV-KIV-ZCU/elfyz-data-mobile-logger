package cz.zcu.kiv.mobile.logger.devices.blood_pressure;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.utils.CloseUtil;


public class BloodPressureDeviceCommunicatorTask extends AsyncTask<Void, Integer, List<BloodPressureMeasurement>> {
  protected final String TAG = BloodPressureDeviceCommunicatorTask.class.getSimpleName();
  
  protected final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  
  protected static final byte MSG_START = 43;
  protected static final byte MSG_A = 37;
  protected static final byte MSG_B = 38;
  protected static final byte MSG_END = 80;
  protected static final byte MSG_ERROR = 84;

  protected BluetoothDevice device;
  protected BloodPressureDeviceListener listener;
  protected boolean justLatest;
  protected boolean turnOffWhenDone;
  
  private BluetoothSocket socket;
  private Exception error;
  
  private List<BloodPressureMeasurement> measurements;
  
  
  public BloodPressureDeviceCommunicatorTask(BluetoothDevice device, BloodPressureDeviceListener listener) {
    this(device, listener, true);
  }
  
  public BloodPressureDeviceCommunicatorTask(BluetoothDevice device, BloodPressureDeviceListener listener, boolean justLatest) {
    this(device, listener, justLatest, true);
  }
  
  public BloodPressureDeviceCommunicatorTask(BluetoothDevice device, BloodPressureDeviceListener listener, boolean justLatest, boolean turnOffWhenDone) {
    if(device == null) throw new NullPointerException("Argument device is null.");
    if(listener == null) throw new NullPointerException("Argument listener is null.");
    
    this.device = device;
    this.listener = listener;
    this.justLatest = justLatest;
    this.turnOffWhenDone = turnOffWhenDone;
    measurements = new ArrayList<BloodPressureMeasurement>();
  }

  
  @Override
  protected List<BloodPressureMeasurement> doInBackground(Void... params) {
    if(isCancelled())
      return measurements;
    
    InputStream in = null;
    OutputStream out = null;
    try {
      byte[] message;
      
      //connect
      socket = device.createInsecureRfcommSocketToServiceRecord(UUID_SPP);
      socket.connect();
      
      in = socket.getInputStream();
      out = socket.getOutputStream();

      if(isCancelled())
        return measurements;
      
      //start communication
      sendMessage(out, new byte[] {81, MSG_START, 0, 0, 0, 0, -93, 0});
      
      message = readMessage(in, MSG_START);

      int recordCount = getRecordCount(message);

      //read measurements
      byte[] msg1 = new byte[]{81, MSG_A, 0, 0, 0, 1, -93, 0};
      byte[] msg2 = new byte[]{81, MSG_B, 0, 0, 0, 1, -93, 0};
      
      for (int record = 0; record < recordCount; record++) {
        if(isCancelled())
          break;
        
        BloodPressureMeasurement measurement = new BloodPressureMeasurement();
        
        msg1[2] = msg2[2] = ((byte) record);
        msg1[3] = msg2[3] = ((byte)(record >> 8));
        
        sendMessage(out, msg1);
        message = readMessage(in, MSG_A);
        getTime(message, measurement);

        sendMessage(out, msg2);
        message = readMessage(in, MSG_B);
        getMeasurement(message, measurement);
        
        msg1[5] = 0;
        measurements.add(measurement);
        
        if(justLatest)
          break; //quit after first measurement if just latest required
      }
      
      if(isCancelled())
        return measurements;

      //end communication (pressure device turns off)
      if(turnOffWhenDone)
        sendMessage(out, new byte[] {81, MSG_END, 0, 0, 0, 0, -93, 0});
    }
    catch(IOException e) {
      Log.e(TAG, "Communication failed.", e);
      error = e;
    }
    catch (CommunicationException e) {
      Log.e(TAG, "Communication protocol failed.", e);
      error = e;
    }
    finally {
      CloseUtil.close(in, out, socket);
    }
    
    return measurements;
  }
  
  @Override
  protected void onPostExecute(List<BloodPressureMeasurement> result) {
    if(error != null) {
      listener.onBPDReadError(error, result);
    }
    else {
      listener.onBPDReadSuccess(result);
    }
    
    super.onPostExecute(result);
  }
  
  @Override
  protected void onCancelled(List<BloodPressureMeasurement> result) {
    listener.onBPDReadCancelled(result);
  }
  

  private byte[] readMessage(InputStream in, byte expectedType) throws CommunicationException, IOException {
    byte[] data = readMessage(in);
    
    if(data[1] != expectedType)
      throw new CommunicationException("Received message is of unexpected type: expected=" + expectedType + ", received=" + data[1]);
    
    return data;
  }

  private byte[] readMessage(InputStream in) throws CommunicationException, IOException {
    byte[] data = new byte[8];
    int read = in.read(data);
    
    if(read != 8 || in.available() > 0)
      throw new CommunicationException("Unexpected read count: expected=8, received=" + (8 + in.available()));
    if(validateMessage(data))
      throw new CommunicationException("Checksum is not valid.");

    return data;
  }

  private void sendMessage(OutputStream out, byte[] message) throws IOException {
    byte[] prepared = prepareMessage(message);
    out.write(prepared);
  }
  
  private boolean validateMessage(byte[] message) {
    int expectedSum = sumMessage(message);
    return ((byte) expectedSum) != message[message.length - 1];
  }

  private byte[] prepareMessage(byte[] message) {
    if(message.length > 1){
      message[message.length - 1] = (byte) sumMessage(message);
    }
    return message;
  }
  
  private int sumMessage(byte[] message){
    int sum = 0;
    for (int i = 0; i < message.length - 1; i++) {
      sum += message[i];
    }
    return sum;
  }
  
  private int getRecordCount(byte[] message) {
    return 0xFF & message[2];
  }

  private void getTime(byte[] message, BloodPressureMeasurement measure) {
    int day = 0xFF & 0x1F & message[2];
    int month = (0xFF & 0x07 & message[2] >> 5) + ((0xFF & 0x01 & message[3]) << 3);
    int year = 2000 + (0xFF & 0x3F & message[3] >> 1);
    int minute = 0x3F & message[4];
    int hour = 0x1F & message[5];

    Calendar time = new GregorianCalendar();
    time.setTimeZone(TimeZone.getTimeZone("UTC"));
    time.set(Calendar.YEAR, year);
    time.set(Calendar.MONTH, month - 1);
    time.set(Calendar.DAY_OF_MONTH, day);
    time.set(Calendar.HOUR_OF_DAY, hour);
    time.set(Calendar.MINUTE, minute);
    time.set(Calendar.SECOND, 0);
    time.set(Calendar.MILLISECOND, 0);

    measure.setTime(time);
  }

  private void getMeasurement(byte[] message, BloodPressureMeasurement measurement) {
    measurement.setSystolicPressure(0xFF & message[2]);
    measurement.setDiastolicPressure(0xFF & message[4]);
    measurement.setMeanPressure(0xFF & message[3]);
    measurement.setHeartRate(0xFF & message[5]);
  }
  
  
  
  public interface BloodPressureDeviceListener {
    void onBPDReadError(Exception error, List<BloodPressureMeasurement> readTillError);
    void onBPDReadSuccess(List<BloodPressureMeasurement> result);
    void onBPDReadCancelled(List<BloodPressureMeasurement> readTillCancel);
  }
}
