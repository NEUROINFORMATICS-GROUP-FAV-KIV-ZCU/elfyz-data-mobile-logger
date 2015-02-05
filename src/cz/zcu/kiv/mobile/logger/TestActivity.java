package cz.zcu.kiv.mobile.logger;

import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import cz.zcu.kiv.mobile.logger.bluetooth.picker.BluetoothDevicePickerActivity;
import cz.zcu.kiv.mobile.logger.data.database.Database;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure.BloodPressureDeviceCommunicatorTask;
import cz.zcu.kiv.mobile.logger.devices.fora.glucose.GlucoseMeasurement;
import cz.zcu.kiv.mobile.logger.devices.fora.glucose.GlucoseMeterDeviceCommunicatorTask;
import cz.zcu.kiv.mobile.logger.devices.fora.glucose.GlucoseMeterDeviceCommunicatorTask.GlucoseMeterDeviceListener;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateActivity;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;

//XXX just for developer testing
public class TestActivity extends Activity implements GlucoseMeterDeviceListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);

    Database db = ((Application) getApplication()).getDatabase();
    
    Intent testIntent = new Intent(this, HeartRateActivity.class);
    try {
      Application.getInstance().setUserProfile(db.getProfileTable().getProfile(1L));
    
//      System.out.println("\nSTARTING TEST ACTIVITY\n");
//      startActivity(testIntent);
//  
//      finish();
      
      Intent pickDeviceIntent = new Intent(this, BluetoothDevicePickerActivity.class);
      startActivityForResult(pickDeviceIntent, 1111);
    }
    catch (DatabaseException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.test, menu);
    return true;
  }
  
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case 1111:
        if(resultCode == RESULT_OK){
          BluetoothDevice device = data.getParcelableExtra(BluetoothDevicePickerActivity.EXTRA_DEVICE);
          if(device != null){
            new GlucoseMeterDeviceCommunicatorTask(device, this).execute();
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
  public void onGDReadError(Exception error,
      List<GlucoseMeasurement> readTillError) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onGDReadSuccess(List<GlucoseMeasurement> result) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onGDReadCancelled(List<GlucoseMeasurement> readTillCancel) {
    // TODO Auto-generated method stub
    
  }

}
