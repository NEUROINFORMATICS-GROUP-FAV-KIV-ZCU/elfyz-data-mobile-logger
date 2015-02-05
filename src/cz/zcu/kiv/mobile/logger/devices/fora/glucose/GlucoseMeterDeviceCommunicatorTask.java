package cz.zcu.kiv.mobile.logger.devices.fora.glucose;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import cz.zcu.kiv.mobile.logger.devices.fora.AForaDeviceCommunicatorTask;
import cz.zcu.kiv.mobile.logger.devices.fora.CommunicationException;


public class GlucoseMeterDeviceCommunicatorTask extends AForaDeviceCommunicatorTask<GlucoseMeasurement, GlucoseMeterDeviceCommunicatorTask.GlucoseMeterDeviceListener> {
  
  
  public GlucoseMeterDeviceCommunicatorTask(BluetoothDevice device, GlucoseMeterDeviceListener listener) {
    super(device, listener, true);
  }
  
  public GlucoseMeterDeviceCommunicatorTask(BluetoothDevice device, GlucoseMeterDeviceListener listener, boolean justLatest) {
    super(device, listener, justLatest, true);
  }
  
  public GlucoseMeterDeviceCommunicatorTask(BluetoothDevice device, GlucoseMeterDeviceListener listener, boolean justLatest, boolean turnOffWhenDone) {
    super(device, listener, justLatest, turnOffWhenDone);
  }


  @Override
  protected void communicate(InputStream in, OutputStream out) throws IOException, CommunicationException {
    byte[] message;
    
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
      
      GlucoseMeasurement measurement = new GlucoseMeasurement();

      msg1[2] = msg2[2] = ((byte) record);
      msg1[3] = msg2[3] = ((byte)(record >> 8));
      
      sendMessage(out, msg1);
      message = readMessage(in, MSG_A);
      measurement.setTime(getTime(message));

      sendMessage(out, msg2);
      message = readMessage(in, MSG_B);
      getMeasurement(message, measurement);
      
      msg1[5] = 0;
      measurements.add(measurement);
      
      if(justLatest)
        break; //quit after first measurement if just latest required
    }
  }
  
  private void getMeasurement(byte[] message, GlucoseMeasurement measurement) {
    measurement.setGlucose(0xFF & message[2] & 255 + (message[3] << 8));
    measurement.setTemperature(0xFF & message[4]);
    measurement.setCode(0xFF & 0x3F & message[5]);
    measurement.setType(0xFF & 0x3 & message[5] >> 6);
  }

  
  @Override
  protected void onPostExecute(List<GlucoseMeasurement> result) {
    if(error != null) {
      listener.onGDReadError(error, result);
    }
    else {
      listener.onGDReadSuccess(result);
    }
    
    super.onPostExecute(result);
  }
  
  @Override
  protected void onCancelled(List<GlucoseMeasurement> result) {
    listener.onGDReadCancelled(result);
  }
  
  
  
  public interface GlucoseMeterDeviceListener {
    void onGDReadError(Exception error, List<GlucoseMeasurement> readTillError);
    void onGDReadSuccess(List<GlucoseMeasurement> result);
    void onGDReadCancelled(List<GlucoseMeasurement> readTillCancel);
  }
}
