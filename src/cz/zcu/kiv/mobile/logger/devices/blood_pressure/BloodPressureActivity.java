package cz.zcu.kiv.mobile.logger.devices.blood_pressure;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.bluetooth.picker.BluetoothDevicePickerActivity;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.data.database.commands.AInsertMeasurementCommand;
import cz.zcu.kiv.mobile.logger.data.database.commands.AInsertMeasurementCommand.InsertCommandListener;
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertBloodPressureMeasurementCommand;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.devices.blood_pressure.BloodPressureDeviceCommunicatorTask.BloodPressureDeviceListener;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public class BloodPressureActivity extends Activity implements BloodPressureDeviceListener, InsertCommandListener {
  private static final String TAG = BloodPressureActivity.class.getSimpleName();
  
  private static final int REQUEST_ENABLE_BT = 1;
  private static final int REQUEST_PICK_BT_DEVICE = 2;
  
  private static final String STATE_INCAPABLE = "state_incapable";
  
  private TextView tvTime;
  private TextView tvSystolic;
  private TextView tvDiastolic;
  private TextView tvMeanPressure;
  private TextView tvHeartRate;
  
  private BluetoothAdapter bt;
  private boolean communicationIncapable = false;
  
  private DateFormat timeFormat;

  private Profile userProfile;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_blood_pressure);

    bt = BluetoothAdapter.getDefaultAdapter();
    if(bt == null){
      Log.e(TAG, "Device does not support Bluetooth.");
      AndroidUtils.toast(this, R.string.alert_device_doesnt_support_bluetooth);
      communicationIncapable = true;
    }
    
    tvTime = (TextView) findViewById(R.id.tv_time);
    tvSystolic = (TextView) findViewById(R.id.tv_systolic);
    tvDiastolic = (TextView) findViewById(R.id.tv_diastolic);
    tvMeanPressure = (TextView) findViewById(R.id.tv_mean_pressure);
    tvHeartRate = (TextView) findViewById(R.id.tv_heart_rate);
    
    userProfile = Application.getInstance().getUserProfileOrLogIn();
    
    if(userProfile == null){
      AndroidUtils.toast(this, R.string.alert_must_be_logged_in);
      Log.e(TAG, "User must be logged in.");
      finish();
      return;
    }
    
    timeFormat = SimpleDateFormat.getDateTimeInstance();
  }

  
  public void startCommunication(View button) {
    if (communicationIncapable) {
      AndroidUtils.toast(this, R.string.alert_communication_not_possible);
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
    startActivity(new Intent(this, BloodPressureListActivity.class));
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
        AndroidUtils.toast(this, R.string.alert_bluetooth_must_be_enabled);
        return;
  
      case REQUEST_PICK_BT_DEVICE:
        if(resultCode == RESULT_OK){
          BluetoothDevice device = data.getParcelableExtra(BluetoothDevicePickerActivity.EXTRA_DEVICE);
          if(device != null){
            new BloodPressureDeviceCommunicatorTask(device, this).execute();
            return;
          }
        }
        AndroidUtils.toast(this, R.string.alert_blood_pressure_device_not_selected);
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
  }
  
  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    communicationIncapable = savedInstanceState.getBoolean(STATE_INCAPABLE);
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
  
  
  @Override
  public void onBPDReadError(Exception error, List<BloodPressureMeasurement> readTillError) {
    AndroidUtils.toast(this, R.string.fail_read_blood_pressure);
  }

  @Override
  public void onBPDReadSuccess(List<BloodPressureMeasurement> result) {
    if (result.isEmpty()) {
      String defaultValue = BloodPressureActivity.this.getString(R.string.value_n_a);
      
      showValues(
          defaultValue,
          defaultValue,
          defaultValue,
          defaultValue,
          defaultValue);
    }
    else {
      BloodPressureMeasurement latest = result.get(0);

      showValues(
          timeFormat.format(latest.getTime().getTime()),
          String.valueOf(latest.getSystolicPressure()),
          String.valueOf(latest.getDiastolicPressure()),
          String.valueOf(latest.getMeanPressure()),
          String.valueOf(latest.getHeartRate()));
      

      new InsertBloodPressureMeasurementCommand(userProfile.getId(), latest, this).execute();
    }
  }

  @Override
  public void onBPDReadCancelled(List<BloodPressureMeasurement> readTillCancel) {}

  @Override
  public void onInsertCommandFinished(AInsertMeasurementCommand<?> command, AsyncTaskResult<Long> result) {
    if(result.getError() != null) {
      Log.e(TAG, "Failed to insert record to DB.", result.getError());
      AndroidUtils.toast(this, R.string.fail_db_insert);
    }
  }
}
