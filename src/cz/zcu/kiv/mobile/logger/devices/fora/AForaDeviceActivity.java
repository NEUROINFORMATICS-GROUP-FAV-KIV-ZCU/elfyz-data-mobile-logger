package cz.zcu.kiv.mobile.logger.devices.fora;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.bluetooth.picker.BluetoothDevicePickerActivity;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public abstract class AForaDeviceActivity extends Activity {
  private static final String TAG = AForaDeviceActivity.class.getSimpleName();
  
  protected static final int REQUEST_ENABLE_BT = 1;
  protected static final int REQUEST_PICK_BT_DEVICE = 2;
  
  protected static final String STATE_INCAPABLE = "state_incapable";
  
  protected BluetoothAdapter bt;
  protected boolean communicationIncapable = false;
  
  protected DateFormat timeFormat;
  protected Profile userProfile;
  

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
            onBluetoothDeviceSelected(device);
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
  
  protected abstract void onBluetoothDeviceSelected(BluetoothDevice device);
}
