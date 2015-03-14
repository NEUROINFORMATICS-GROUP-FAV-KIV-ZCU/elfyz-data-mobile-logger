package cz.zcu.kiv.mobile.logger.bluetooth.picker;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;


public class BluetoothDevicePickerActivity extends Activity {
  public static final String EXTRA_DEVICE = "picked.bluetooth.device";
  public static final int RESULT_ERROR = 100;
  private static final int REQUEST_PICK_DEVICE = 200;

  private BluetoothAdapter bt;
  private BTDevicePickReceiver pickerReceiver;
  private boolean registered = false;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    bt = BluetoothAdapter.getDefaultAdapter();
    if(bt == null){
      setResult(RESULT_ERROR);
      finish();
    }

    pickerReceiver = new BTDevicePickReceiver(this);
    
    registerReceiver();
    
    startActivityForResult(
        new Intent(IBluetoothDevicePicker.ACTION_LAUNCH)
          .putExtra(IBluetoothDevicePicker.EXTRA_NEED_AUTH, false)
          .putExtra(IBluetoothDevicePicker.EXTRA_FILTER_TYPE, IBluetoothDevicePicker.FILTER_TYPE_ALL)
          .setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS),
        REQUEST_PICK_DEVICE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode == REQUEST_PICK_DEVICE){
      unregisterReceiver();
      finish();
    }
    else{
      super.onActivityResult(requestCode, resultCode, data);
    }
  }
  
  @Override
  protected void onDestroy() {
    unregisterReceiver();
    super.onDestroy();
  }

  private void registerReceiver() {
    if(!registered) {
      registerReceiver(pickerReceiver, new IntentFilter(IBluetoothDevicePicker.ACTION_DEVICE_SELECTED));
      registered = true;
    }
  }

  private void unregisterReceiver() {
    if(registered) {
      unregisterReceiver(pickerReceiver);
      registered = false;
    }
  }
  

  
  private class BTDevicePickReceiver extends BroadcastReceiver {
    private Activity picker;
    
    public BTDevicePickReceiver(Activity picker) {
      this.picker = picker;  
    }

    @Override
    public void onReceive(Context context, Intent intent)  {
      if(IBluetoothDevicePicker.ACTION_DEVICE_SELECTED.equals(intent.getAction())) {
        unregisterReceiver(); //Immediately unregister
        BluetoothDevice pickedDevice = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        
        Intent data = new Intent();
        data.putExtra(EXTRA_DEVICE, pickedDevice);
        
        picker.setResult(RESULT_OK, data);
        picker.finish();
      }
    }
  }
}
