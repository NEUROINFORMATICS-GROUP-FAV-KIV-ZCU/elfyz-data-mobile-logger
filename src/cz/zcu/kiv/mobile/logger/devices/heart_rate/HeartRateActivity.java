package cz.zcu.kiv.mobile.logger.devices.heart_rate;

import java.math.BigDecimal;
import java.util.EnumSet;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.DataState;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.RrFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;

import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.devices.DeviceListActivity;
import cz.zcu.kiv.mobile.logger.service.DeviceCommunicatorService;
import cz.zcu.kiv.mobile.logger.service.DeviceCommunicatorService.DeviceCommunicatorBinder;
import cz.zcu.kiv.mobile.logger.service.communicators.heart_rate.HeartRateCommunicator.HeartRateListener;


public class HeartRateActivity extends Activity implements ServiceConnection, HeartRateListener {
  private static final String TAG = HeartRateActivity.class.getSimpleName();

  protected TextView tvHeartRate;
  protected TextView tvDataStatus;
  protected TextView tvRateInterval;
  protected TextView tvBeatCount;
  protected TextView tvStatus;

  private Profile userProfile;
  
  private DeviceCommunicatorBinder service;
  private boolean listening = false;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_heart_rate);
    
    tvStatus = (TextView) findViewById(R.id.tv_status);
    tvHeartRate = (TextView) findViewById(R.id.tv_heart_rate);
    tvDataStatus = (TextView) findViewById(R.id.tv_data_status);
    tvBeatCount = (TextView) findViewById(R.id.tv_beat_count);
    tvRateInterval = (TextView) findViewById(R.id.tv_rate_interval);
    
    userProfile = getIntent().getParcelableExtra(DeviceListActivity.EXTRA_USER_PROFILE);
    
    if(userProfile == null){
      Toast.makeText(this, R.string.alert_activity_not_launched_correctly, Toast.LENGTH_LONG).show();
      Log.e(TAG, "User profile could not be retrieved from intent: extra name=" + DeviceListActivity.EXTRA_USER_PROFILE);
      finish();
      return;
    }
    
    connectService();
  }
  
  @Override
  protected void onPause() {
    super.onPause();
    unsubscribe();
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    subscribe();
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    disconnectService();
  }
  
  @Override
  public void onServiceConnected(ComponentName name, IBinder binder) {
    service = (DeviceCommunicatorBinder) binder;
    subscribe();  //TODO this vs. subscribing in onPause
  }
  
  @Override
  public void onServiceDisconnected(ComponentName name) {
    Log.i(TAG, "Service has been disconnected.");
    setStatus("Služba odpojena.", false);
    unsubscribe();
  }
  
  private void connectService() {
    if(service != null) {
      Toast.makeText(this, "Služba je již připojena.", Toast.LENGTH_LONG).show();
      return;
    }
    
    boolean bound = bindService(new Intent(this, DeviceCommunicatorService.class), this, Context.BIND_AUTO_CREATE);
    
    if(!bound) {
      setStatus("Chyba služby.", false);
      Toast.makeText(this, "Nepodařilo se připojit ke službě.", Toast.LENGTH_LONG).show();
    }
  }
  
  private void disconnectService() {
    if(service != null) {
      unsubscribe();
      service = null;
    }
    unbindService(this);
  }
  
  private void subscribe() {
    if(!listening && service != null) {
      service.startHeartRate(this, this);
      listening = true;
    }
  }
  
  private void unsubscribe() {
    if(listening && service != null) {
      service.stopHeartRate(this);
      listening = false;
    }
  }
  
  protected void setStatus(final String status, boolean ensureUiThread) {
    if(ensureUiThread) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          tvStatus.setText(status);
        }
      });
    }
    else {
      tvStatus.setText(status);
    }
  }

  protected void setStatus(final int stringID, boolean ensureUiThread) {
    if(ensureUiThread) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          tvStatus.setText(stringID);
        }
      });
    }
    else {
      tvStatus.setText(stringID);
    }
  }

  
  @Override
  public void onDeviceConnected(String deviceName) {
    setStatus(getString(R.string.watching_) + deviceName, true);
  }

  @Override
  public void onDeviceStateChange(DeviceState state) {
    // TODO Auto-generated method stub
  }

  @Override
  public void onConnectionError(RequestAccessResult resultCode) {
    setStatus("Connection failed: " + resultCode, true);
  }

  @Override
  public void onHeartRateDataReceived(long estTimestamp, EnumSet<EventFlag> eventFlags, final int computedHeartRate,
      final long heartBeatCount, BigDecimal heartBeatEventTime, final DataState dataState) {

    runOnUiThread(new Runnable() {
      public void run() {
        tvHeartRate.setText(
            (dataState.equals(DataState.LIVE_DATA))
            ? String.valueOf(computedHeartRate)
                : getString(R.string.value_n_a)
            );
        tvDataStatus.setText(dataState.toString());
        tvBeatCount.setText(String.valueOf(heartBeatCount));
      }
    });
  }

  @Override
  public void onAdditionalDataReceived(long estTimestamp, EnumSet<EventFlag> eventFlags, int manufacturerSpecificByte, BigDecimal previousHeartBeatEventTime) { }

  @Override
  public void onCumulativeOperatingTimeReceived(long estTimestamp, EnumSet<EventFlag> eventFlags, long cumulativeOperatingTime) { }

  @Override
  public void onManufacturerAndSerialReceived(long estTimestamp, EnumSet<EventFlag> eventFlags, int manufacturerID, int serialNumber) { }

  @Override
  public void onVersionAndModelReceived(long estTimestamp, EnumSet<EventFlag> eventFlags, int hardwareVersion, int softwareVersion, int modelNumber) { }

  @Override
  public void onCalculatedRrIntervalReceived(long estTimestamp, EnumSet<EventFlag> eventFlags,
      final BigDecimal calculatedRrInterval, RrFlag rrFlag) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        tvRateInterval.setText(calculatedRrInterval.toString());
      }
    });
  }

  @Override
  public void onConnectionClosed() {
    setStatus("Disconnected", true);
  }
}
