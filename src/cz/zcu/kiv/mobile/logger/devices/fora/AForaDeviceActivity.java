package cz.zcu.kiv.mobile.logger.devices.fora;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.bluetooth.picker.BluetoothDevicePickerActivity;
import cz.zcu.kiv.mobile.logger.common.UserActivity;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public abstract class AForaDeviceActivity extends UserActivity {
  private static final String TAG = AForaDeviceActivity.class.getSimpleName();
  
  protected static final int REQUEST_ENABLE_BT = 1;
  protected static final int REQUEST_PICK_BT_DEVICE = 2;
  
  protected static final String STATE_INCAPABLE = "state_incapable";
  protected static final String STATE_BT_DEVICE = "state_bt_device";
  
  protected BluetoothAdapter bt;
  protected boolean communicationIncapable = false;
  protected BluetoothDevice btDevice;
  
  protected DateFormat timeFormat;
  

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
    
    timeFormat = SimpleDateFormat.getDateTimeInstance();
  }

  
  public void selectBtDevice(View button) {
    if (communicationIncapable) {
      AndroidUtils.toast(this, R.string.alert_communication_not_possible);
    }
    else if(bt.isEnabled()) {
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
        break;
  
      case REQUEST_PICK_BT_DEVICE:
        if(resultCode == RESULT_OK){
          BluetoothDevice device = data.getParcelableExtra(BluetoothDevicePickerActivity.EXTRA_DEVICE);
          if(device != null) {
            setBtDevice(device);
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
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.action_select_bt:
      selectBtDevice(null);
      return true;
    case R.id.action_load_all_records:
      loadRecords(false);
      return true;

    default:
      return super.onOptionsItemSelected(item);
    }
  }


  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(STATE_INCAPABLE, communicationIncapable);
    if(btDevice != null)
      outState.putParcelable(STATE_BT_DEVICE, btDevice);
  }
  
  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    communicationIncapable = savedInstanceState.getBoolean(STATE_INCAPABLE);
    setBtDevice(savedInstanceState.<BluetoothDevice>getParcelable(STATE_BT_DEVICE));
  }
  
  private void setBtDevice(BluetoothDevice device) {
    if(device != null) {
      btDevice = device;
      getActionBar().setSubtitle(device.getName());
    }
  }


  public void loadLastRecord(View view) {
    loadRecords(true);
  }

  protected abstract void loadRecords(boolean justLatest);
}
