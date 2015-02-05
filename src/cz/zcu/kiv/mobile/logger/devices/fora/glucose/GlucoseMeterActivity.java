package cz.zcu.kiv.mobile.logger.devices.fora.glucose;

import java.util.List;

import android.bluetooth.BluetoothDevice;
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
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertGlucoseMeasurementCommand;
import cz.zcu.kiv.mobile.logger.devices.fora.AForaDeviceActivity;
import cz.zcu.kiv.mobile.logger.devices.fora.glucose.GlucoseMeterDeviceCommunicatorTask.GlucoseMeterDeviceListener;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public class GlucoseMeterActivity extends AForaDeviceActivity implements GlucoseMeterDeviceListener, InsertCommandListener {
  private static final String TAG = GlucoseMeterActivity.class.getSimpleName();
  
  private TextView tvTime;
  private TextView tvGlucose;
  private TextView tvTemperature;
  private TextView tvCode;
  private TextView tvType;
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_glucose_meter);
    
    tvTime = (TextView) findViewById(R.id.tv_time);
    tvGlucose = (TextView) findViewById(R.id.tv_glucose);
    tvTemperature = (TextView) findViewById(R.id.tv_temperature);
    tvCode = (TextView) findViewById(R.id.tv_code);
    tvType = (TextView) findViewById(R.id.tv_type);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.glucose_meter, menu);
    return true;
  }


  public void showAllRecords(View button){
    startActivity(new Intent(this, GlucoseMeterListActivity.class));
  }

  @Override
  protected void onBluetoothDeviceSelected(BluetoothDevice device) {
    new GlucoseMeterDeviceCommunicatorTask(device, this).execute();
  }
  
  private void showValues(String time, String glucose, String temperature, String code, String type){
    tvTime.setText(time);
    tvGlucose.setText(glucose);
    tvTemperature.setText(temperature);
    tvCode.setText(code);
    tvType.setText(type);
  }
  
  
  @Override
  public void onGDReadError(Exception error, List<GlucoseMeasurement> readTillError) {
    AndroidUtils.toast(this, R.string.fail_read_device_data);
  }

  @Override
  public void onGDReadSuccess(List<GlucoseMeasurement> result) {
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
      GlucoseMeasurement latest = result.get(0);

      showValues(
          timeFormat.format(latest.getTime().getTime()),
          String.valueOf(latest.getGlucose()),
          String.valueOf(latest.getTemperature()),
          String.valueOf(latest.getCode()),
          String.valueOf(latest.getType()));
      

      new InsertGlucoseMeasurementCommand(userProfile.getId(), latest, this).execute();
    }
  }

  @Override
  public void onGDReadCancelled(List<GlucoseMeasurement> readTillCancel) { }

  @Override
  public void onInsertCommandFinished(AInsertMeasurementCommand<?> command, AsyncTaskResult<Long> result) {
    if(result.getError() != null) {
      Log.e(TAG, "Failed to insert record to DB.", result.getError());
      AndroidUtils.toast(this, R.string.fail_db_insert);
    }
  }
}
