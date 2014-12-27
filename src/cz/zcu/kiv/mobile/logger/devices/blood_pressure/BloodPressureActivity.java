package cz.zcu.kiv.mobile.logger.devices.blood_pressure;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.bluetooth.picker.BluetoothDevicePickerActivity;
import cz.zcu.kiv.mobile.logger.data.database.Database;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.devices.DeviceListActivity;
import cz.zcu.kiv.mobile.logger.utils.CloseUtil;


public class BloodPressureActivity extends Activity {
  private static final String TAG = BloodPressureActivity.class.getSimpleName();
  private static final int REQUEST_ENABLE_BT = 1;
  private static final int REQUEST_PICK_BT_DEVICE = 2;
  private static final String STATE_INCAPABLE = "state_incapable";
  private static final String EXTRA_MEASUREMENTS = "state_measurements";
  
  private TextView tvTime;
  private TextView tvSystolic;
  private TextView tvDiastolic;
  private TextView tvMeanPressure;
  private TextView tvHeartRate;
  
  
  private boolean communicationIncapable = false;
  private ArrayList<BloodPressureMeasurement> measurements = new ArrayList<BloodPressureMeasurement>();
  
  private BluetoothAdapter bt;
  private SimpleDateFormat timeFormat;

  private Database db;
  private Profile userProfile;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_blood_pressure);

    bt = BluetoothAdapter.getDefaultAdapter();
    if(bt == null){
      Log.e(TAG, "Device does not support Bluetooth.");
      Toast.makeText(this, R.string.alert_device_doesnt_support_bluetooth, Toast.LENGTH_LONG).show();
      communicationIncapable = true;
    }
    
    tvTime = (TextView) findViewById(R.id.tv_time);
    tvSystolic = (TextView) findViewById(R.id.tv_systolic);
    tvDiastolic = (TextView) findViewById(R.id.tv_diastolic);
    tvMeanPressure = (TextView) findViewById(R.id.tv_mean_pressure);
    tvHeartRate = (TextView) findViewById(R.id.tv_heart_rate);
    
    userProfile = getIntent().getParcelableExtra(DeviceListActivity.EXTRA_USER_PROFILE);
    
    if(userProfile == null){
      Toast.makeText(this, R.string.alert_activity_not_launched_correctly, Toast.LENGTH_LONG).show();
      Log.e(TAG, "User profile could not be retrieved from intent: extra name=" + DeviceListActivity.EXTRA_USER_PROFILE);
      finish();
      return;
    }
    
    timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    
    db = ((Application) getApplication()).getDatabase();
  }

  
  public void startCommunication(View button) {
    if (communicationIncapable) {
      Toast.makeText(this, R.string.alert_communication_not_possible, Toast.LENGTH_LONG).show();
    }
    else if(bt.isEnabled()) {
      //TODO tlačítko na výběr BT device (nebo takto vždy při každém čtení?)
      Intent pickDeviceIntent = new Intent(this, BluetoothDevicePickerActivity.class);
      startActivityForResult(pickDeviceIntent, REQUEST_PICK_BT_DEVICE);
    }
    else {
      Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
    }
  }

  public void showAllRecords(View button){
    Intent showListIntent = new Intent(this, BloodPressureListActivity.class);
    showListIntent.putExtra(BloodPressureListActivity.EXTRA_MEASUREMENTS, measurements);
    startActivity(showListIntent);
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case REQUEST_ENABLE_BT:
        if(resultCode == RESULT_OK && bt.isEnabled()) {
          Intent pickDeviceIntent = new Intent(this, BluetoothDevicePickerActivity.class);
          startActivityForResult(pickDeviceIntent, REQUEST_PICK_BT_DEVICE);
          return;
        }
        Toast.makeText(this, R.string.alert_bluetooth_must_be_enabled, Toast.LENGTH_LONG).show();
        return;
  
      case REQUEST_PICK_BT_DEVICE:
        if(resultCode == RESULT_OK){
          BluetoothDevice device = data.getParcelableExtra(BluetoothDevicePickerActivity.EXTRA_DEVICE);
          if(device != null){
            new BloodPressureDeviceCommunicatorTask(device, false).execute();
            return;
          }
        }
        Toast.makeText(this, R.string.alert_blood_pressure_device_not_selected, Toast.LENGTH_LONG).show();
        return;
        
      default:
        super.onActivityResult(requestCode, resultCode, data);
        break;
    }
  }
  
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    
    outState.putBoolean(STATE_INCAPABLE, communicationIncapable);
    outState.putParcelableArrayList(EXTRA_MEASUREMENTS, measurements);
  }
  
  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    
    communicationIncapable = savedInstanceState.getBoolean(STATE_INCAPABLE);
    measurements = savedInstanceState.getParcelableArrayList(EXTRA_MEASUREMENTS);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.blood_pressure, menu);
    return true;
  }
  
  private void showValues(String time, String systolic, String diastolic, String mean, String heartRate){
    tvTime.setText(time);
    tvSystolic.setText(systolic);
    tvDiastolic.setText(diastolic);
    tvMeanPressure.setText(mean);
    tvHeartRate.setText(heartRate);
  }
  
  
  //TODO move task to service? handle configuration changes!
  private class BloodPressureDeviceCommunicatorTask extends AsyncTask<Void, Integer, List<BloodPressureMeasurement>>{
    private final String TAG = BloodPressureDeviceCommunicatorTask.class.getSimpleName();
    
    private final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    private static final byte MSG_START = 43;
    private static final byte MSG_A = 37;
    private static final byte MSG_B = 38;
    private static final byte MSG_END = 80;
    private static final byte MSG_ERROR = 84;

    private BluetoothDevice device;
    private boolean justLatest;
    
    private BluetoothSocket socket;
    private Exception error;
    
    private List<BloodPressureMeasurement> measurements;
    
    
    public BloodPressureDeviceCommunicatorTask(BluetoothDevice device, boolean justLatest) {
      this.device = device;
      this.justLatest = justLatest;
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
        socket.connect(); //TODO custom cancel with socket close?
        
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
            return measurements;  //why do they lie with the leak, finally should get this, shouldn't it? :)
          
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

        //end communication (pressure device turns off)
        sendMessage(out, new byte[] {81, MSG_END, 0, 0, 0, 0, -93, 0});
      }
      catch(IOException e) {
        Log.e(TAG, "Communication failed.", error);
        error = e;
      }
      catch (CommunicationException e) {
        Log.e(TAG, "Communication protocol failed.", error);
        error = e;
      }
      finally {
        CloseUtil.close(in, out, socket);
      }
      
      return measurements;
    }
    
    @Override
    protected void onPostExecute(List<BloodPressureMeasurement> result) {
      //on error
      if(error != null){  //TODO probably don't show exception message
        Toast.makeText(BloodPressureActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        return;
      }
      
      //on success
      if (result.isEmpty()) { //TODO callbacks/ broadcasts from service...
        final String defaultValue = BloodPressureActivity.this.getString(R.string.value_n_a);
        
        showValues(
            defaultValue,
            defaultValue,
            defaultValue,
            defaultValue,
            defaultValue);
      }
      else {
        final BloodPressureMeasurement latest = result.get(0);
  
        showValues(
            timeFormat.format(latest.getTime().getTime()),
            String.valueOf(latest.getSystolicPressure()),
            String.valueOf(latest.getDiastolicPressure()),
            String.valueOf(latest.getMeanPressure()),
            String.valueOf(latest.getHeartRate()));
        
        saveToDB(latest); //TODO zni�it viz saveToDB
      }
      
      super.onPostExecute(result);
    }
    
    
    private void saveToDB(BloodPressureMeasurement latest) {  //TODO jinde, dal�� async task nebo l�p content provider/loader
      try {
        db.addBloodPressureMeasurement(userProfile.getId(), latest);
      }
      catch (DatabaseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
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
  }
}
