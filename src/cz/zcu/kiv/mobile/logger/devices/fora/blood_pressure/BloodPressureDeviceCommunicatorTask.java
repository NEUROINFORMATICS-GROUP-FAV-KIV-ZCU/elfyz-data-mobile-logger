package cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import cz.zcu.kiv.mobile.logger.devices.fora.AForaDeviceCommunicatorTask;
import cz.zcu.kiv.mobile.logger.devices.fora.CommunicationException;


public class BloodPressureDeviceCommunicatorTask extends AForaDeviceCommunicatorTask<BloodPressureMeasurement, BloodPressureDeviceCommunicatorTask.BloodPressureDeviceListener> {

  
  public BloodPressureDeviceCommunicatorTask(BluetoothDevice device, BloodPressureDeviceListener listener) {
    super(device, listener, true);
  }
  
  public BloodPressureDeviceCommunicatorTask(BluetoothDevice device, BloodPressureDeviceListener listener, boolean justLatest) {
    super(device, listener, justLatest, true);
  }
  
  public BloodPressureDeviceCommunicatorTask(BluetoothDevice device, BloodPressureDeviceListener listener, boolean justLatest, boolean turnOffWhenDone) {
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
      
      BloodPressureMeasurement measurement = new BloodPressureMeasurement();
      
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

  private void getMeasurement(byte[] message, BloodPressureMeasurement measurement) {
    measurement.setSystolicPressure(0xFF & message[2]);
    measurement.setDiastolicPressure(0xFF & message[4]);
    measurement.setMeanPressure(0xFF & message[3]);
    measurement.setHeartRate(0xFF & message[5]);
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
  
  
  
  public interface BloodPressureDeviceListener {
    void onBPDReadError(Exception error, List<BloodPressureMeasurement> readTillError);
    void onBPDReadSuccess(List<BloodPressureMeasurement> result);
    void onBPDReadCancelled(List<BloodPressureMeasurement> readTillCancel);
  }
}
