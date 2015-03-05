package cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.data.database.commands.AInsertMeasurementCommand;
import cz.zcu.kiv.mobile.logger.data.database.commands.AInsertMeasurementCommand.InsertCommandListener;
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertBloodPressureMeasurementCommand;
import cz.zcu.kiv.mobile.logger.devices.fora.AForaDeviceActivity;
import cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure.BloodPressureDeviceCommunicatorTask.BloodPressureDeviceListener;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public class BloodPressureActivity extends AForaDeviceActivity implements BloodPressureDeviceListener, InsertCommandListener {
  private static final String TAG = BloodPressureActivity.class.getSimpleName();
  
  private TextView tvTime;
  private TextView tvSystolic;
  private TextView tvDiastolic;
  private TextView tvMeanPressure;
  private TextView tvHeartRate;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_blood_pressure);

    tvTime = (TextView) findViewById(R.id.tv_time);
    tvSystolic = (TextView) findViewById(R.id.tv_systolic);
    tvDiastolic = (TextView) findViewById(R.id.tv_diastolic);
    tvMeanPressure = (TextView) findViewById(R.id.tv_mean_pressure);
    tvHeartRate = (TextView) findViewById(R.id.tv_heart_rate);
    
    getActionBar().setSubtitle(R.string.no_device_selected);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.blood_pressure, menu);
    return true;
  }
  
  @Override
  protected void loadRecords(boolean justLatest) {
    if(btDevice == null) {
      AndroidUtils.toast(this, R.string.fail_bt_not_selected);
    }
    else {
      new BloodPressureDeviceCommunicatorTask(btDevice, this, justLatest).execute();
    }
  }

  public void showAllRecords(View button){
    startActivity(new Intent(this, BloodPressureListActivity.class));
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
    AndroidUtils.toast(this, R.string.fail_read_device_data);
  }

  @Override
  public void onBPDReadSuccess(List<BloodPressureMeasurement> result) {
    if (result.isEmpty()) {
      String defaultValue = getString(R.string.value_n_a);
      
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
