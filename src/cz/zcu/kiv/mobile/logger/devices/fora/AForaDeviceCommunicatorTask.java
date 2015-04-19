package cz.zcu.kiv.mobile.logger.devices.fora;

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
import cz.zcu.kiv.mobile.logger.data.types.blood_pressure.BloodPressureMeasurement;
import cz.zcu.kiv.mobile.logger.utils.CloseUtil;


public abstract class AForaDeviceCommunicatorTask<T_Data, T_Listener> extends AsyncTask<Void, Integer, List<T_Data>> {
  private static final String TAG = AForaDeviceCommunicatorTask.class.getSimpleName();
  
  protected final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  
  protected static final byte MSG_START = 43;
  protected static final byte MSG_A = 37;
  protected static final byte MSG_B = 38;
  protected static final byte MSG_END = 80;
  protected static final byte MSG_ERROR = 84;

  protected BluetoothDevice device;
  protected T_Listener listener;
  protected boolean justLatest;
  protected boolean turnOffWhenDone;
  
  protected BluetoothSocket socket;
  protected Exception error;
  
  protected List<T_Data> measurements;
  
  
  public AForaDeviceCommunicatorTask(BluetoothDevice device, T_Listener listener) {
    this(device, listener, true);
  }
  
  public AForaDeviceCommunicatorTask(BluetoothDevice device, T_Listener listener, boolean justLatest) {
    this(device, listener, justLatest, true);
  }
  
  public AForaDeviceCommunicatorTask(BluetoothDevice device, T_Listener listener, boolean justLatest, boolean turnOffWhenDone) {
    if(device == null) throw new NullPointerException("Argument device is null.");
    if(listener == null) throw new NullPointerException("Argument listener is null.");
    
    this.device = device;
    this.listener = listener;
    this.justLatest = justLatest;
    this.turnOffWhenDone = turnOffWhenDone;
    measurements = new ArrayList<T_Data>();
  }

  
  @Override
  protected List<T_Data> doInBackground(Void... params) {
    if(isCancelled())
      return measurements;
    
    InputStream in = null;
    OutputStream out = null;
    try {
      //connect
      socket = device.createInsecureRfcommSocketToServiceRecord(UUID_SPP);
      socket.connect();
      
      in = socket.getInputStream();
      out = socket.getOutputStream();

      if(isCancelled())
        return measurements;
      
      communicate(in, out);
      
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
  
  
  /**
   * Should just populate <code>measurements</code>. Periodically check isCancelled method and return if true.
   * @param in
   * @param out
   * @throws IOException
   * @throws CommunicationException
   */
  protected abstract void communicate(InputStream in, OutputStream out) throws IOException, CommunicationException;

  
  protected byte[] readMessage(InputStream in, byte expectedType) throws CommunicationException, IOException {
    byte[] data = readMessage(in);
    
    if(data[1] != expectedType)
      throw new CommunicationException("Received message is of unexpected type: expected=" + expectedType + ", received=" + data[1]);
    
    return data;
  }

  protected byte[] readMessage(InputStream in) throws CommunicationException, IOException {
    byte[] data = new byte[8];
    int read = in.read(data);
    
    if(read != 8 || in.available() > 0)
      throw new CommunicationException("Unexpected read count: expected=8, received=" + (8 + in.available()));
    if(validateMessage(data))
      throw new CommunicationException("Checksum is not valid.");

    return data;
  }

  protected void sendMessage(OutputStream out, byte[] message) throws IOException {
    byte[] prepared = prepareMessage(message);
    out.write(prepared);
  }
  
  protected boolean validateMessage(byte[] message) {
    int expectedSum = sumMessage(message);
    return ((byte) expectedSum) != message[message.length - 1];
  }

  protected byte[] prepareMessage(byte[] message) {
    if(message.length > 1){
      message[message.length - 1] = (byte) sumMessage(message);
    }
    return message;
  }
  
  protected int sumMessage(byte[] message){
    int sum = 0;
    for (int i = 0; i < message.length - 1; i++) {
      sum += message[i];
    }
    return sum;
  }
  
  protected int getRecordCount(byte[] message) {
    return 0xFF & message[2];
  }

  protected Calendar getTime(byte[] message) {
    int day = 0xFF & 0x1F & message[2];
    int month = (0xFF & 0x07 & message[2] >> 5) + ((0xFF & 0x01 & message[3]) << 3);
    int year = 2000 + (0xFF & 0x3F & message[3] >> 1);
    int minute = 0x3F & message[4];
    int hour = 0x1F & message[5];

    Calendar time = new GregorianCalendar();
    time.setTimeZone(TimeZone.getTimeZone("UTC"));  //TODO select timezone
    time.set(Calendar.YEAR, year);
    time.set(Calendar.MONTH, month - 1);
    time.set(Calendar.DAY_OF_MONTH, day);
    time.set(Calendar.HOUR_OF_DAY, hour);
    time.set(Calendar.MINUTE, minute);
    time.set(Calendar.SECOND, 0);
    time.set(Calendar.MILLISECOND, 0);

    return time;
  }

  
  
  public interface ForaDeviceListener {
    void onBPDReadError(Exception error, List<BloodPressureMeasurement> readTillError);
    void onBPDReadSuccess(List<BloodPressureMeasurement> result);
    void onBPDReadCancelled(List<BloodPressureMeasurement> readTillCancel);
  }
}
