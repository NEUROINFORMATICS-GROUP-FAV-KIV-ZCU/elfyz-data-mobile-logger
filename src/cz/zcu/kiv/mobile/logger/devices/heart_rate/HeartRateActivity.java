package cz.zcu.kiv.mobile.logger.devices.heart_rate;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.DataState;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertHeartRateMeasurementCommand;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.service.DeviceCommunicatorService;
import cz.zcu.kiv.mobile.logger.service.DeviceCommunicatorService.DeviceCommunicatorBinder;
import cz.zcu.kiv.mobile.logger.service.communicators.heart_rate.HeartRateCommunicator.HeartRateListener;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public class HeartRateActivity extends Activity implements ServiceConnection, HeartRateListener {
  private static final String TAG = HeartRateActivity.class.getSimpleName();
  
  private static final String STATE_LISTEN = "listen";

  protected TextView tvHeartRate;
  protected TextView tvDataStatus;
  protected TextView tvRateInterval;
  protected TextView tvBeatCount;
  protected TextView tvStatus;
  protected Button bToggleListening;

  protected InsertHeartRateMeasurementCommand insertCommand;
  protected Profile userProfile;
  
  private DeviceCommunicatorBinder service;
  private boolean listening = false;
  private boolean listen = false;
  
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_heart_rate);
    
    tvStatus = (TextView) findViewById(R.id.tv_status);
    tvHeartRate = (TextView) findViewById(R.id.tv_heart_rate);
    tvDataStatus = (TextView) findViewById(R.id.tv_data_status);
    tvBeatCount = (TextView) findViewById(R.id.tv_beat_count);
    tvRateInterval = (TextView) findViewById(R.id.tv_rate_interval);
    bToggleListening = (Button) findViewById(R.id.b_toggle_listening);
    
    userProfile = Application.getInstance().getUserProfileOrLogIn();
    
    if(userProfile == null) {
      AndroidUtils.toast(this, R.string.alert_must_be_logged_in);
      Log.e(TAG, "User must be logged in.");
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
    if(listen) {
      subscribe();
    }
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    disconnectService();
  }
  
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(STATE_LISTEN, listen);
  }
  
  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    listen = savedInstanceState.getBoolean(STATE_LISTEN);
  }

  @Override
  public void onServiceConnected(ComponentName name, IBinder binder) {
    service = (DeviceCommunicatorBinder) binder;
    if(listen) {
      subscribe();
    }
  }
  
  @Override
  public void onServiceDisconnected(ComponentName name) {
    Log.i(TAG, "Service has been disconnected.");
    setStatus(R.string.service_disconnected, false);
    unsubscribe();
  }
  
  private void connectService() {
    if(service != null) {
      AndroidUtils.toast(this, R.string.alert_service_already_connected);
      return;
    }
    
    boolean bound = bindService(new Intent(this, DeviceCommunicatorService.class), this, Context.BIND_AUTO_CREATE);
    
    if(!bound) {
      setStatus(R.string.service_error, false);
      AndroidUtils.toast(this, R.string.fail_connect_service);
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
  
  public void toggleListening(View view) {
    if(listen) {
      listen = false;
      unsubscribe();
      bToggleListening.setText(R.string.start_listening);
    }
    else {
      listen = true;
      subscribe();
      bToggleListening.setText(R.string.stop_listening);
    }
  }
  
  public void showAllRecords(View view) {
    startActivity(new Intent(this, HeartRateDataListActivity.class));
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
    setStatus(getString(R.string.fail_connection_) + resultCode, true);
  }

  @Override
  public void onHeartRateDataReceived(final HeartRateMeasurement measurement) {
    runOnUiThread(new Runnable() {
      public void run() {
        tvHeartRate.setText(
            (measurement.getDataState().equals(DataState.LIVE_DATA))
            ? String.valueOf(measurement.getComputedHeartRate())
                : getString(R.string.value_n_a)
            );
        tvDataStatus.setText(measurement.getDataState().toString());
        tvBeatCount.setText(String.valueOf(measurement.getHeartBeatCount()));
      }
    });
  }

  @Override
  public void onAdditionalDataReceived(HeartRatePage4 data) { }

  @Override
  public void onCumulativeOperatingTimeReceived(HeartRateCumulativeOperatingTime data) { }

  @Override
  public void onManufacturerAndSerialReceived(HeartRateManufacturerAndSerial data) { }

  @Override
  public void onVersionAndModelReceived(HeartRateVersionAndModel data) { }

  @Override
  public void onCalculatedRrIntervalReceived(final HeartRateCalculatedRrInterval data) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        tvRateInterval.setText(data.getCalculatedRrInterval().toString());
      }
    });
  }

  @Override
  public void onConnectionClosed() {
    setStatus(R.string.disconnected, true);
  }
}
