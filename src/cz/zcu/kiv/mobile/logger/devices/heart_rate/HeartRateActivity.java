package cz.zcu.kiv.mobile.logger.devices.heart_rate;

import java.math.BigDecimal;
import java.util.EnumSet;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.DataState;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.ICalculatedRrIntervalReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.IHeartRateDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.IPage4AddtDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.RrFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.ICumulativeOperatingTimeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.IManufacturerAndSerialReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.IVersionAndModelReceiver;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.database.Database;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.devices.DeviceListActivity;


public class HeartRateActivity extends Activity {
  private static final String TAG = HeartRateActivity.class.getSimpleName();

  protected PccReleaseHandle<AntPlusHeartRatePcc> releaseHandle;
  protected AntPlusHeartRatePcc heartRateDevice;
  
  protected TextView tvHeartRate;
  protected TextView tvDataStatus;
  protected TextView tvRateInterval;
  protected TextView tvBeatCount;
  protected TextView tvStatus;

  private Database db;
  private Profile userProfile;
  

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
    }
    
    db = ((Application) getApplication()).getDatabase();

    releaseHandle = AntPlusHeartRatePcc.requestAccess(this, this, pluginAccessResultReceiver, deviceStateChangeReceiver);
  }


  protected void setStatus(final String status) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        tvStatus.setText(status);
      }
    });
  }

  protected void setStatus(final int stringID) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        tvStatus.setText(getString(stringID));
      }
    });
  }

  protected void close(){
    if(releaseHandle != null){
      releaseHandle.close();
    }
  }
  
  @Override
  protected void onDestroy() {
    close();
    super.onDestroy();
  }


  protected void subscribeToHrEvents() {  //TODO move to service and use local broadcast messages
    heartRateDevice.subscribeHeartRateDataEvent(new IHeartRateDataReceiver() {
      @Override
      public void onNewHeartRateData(long estTimestamp, EnumSet<EventFlag> eventFlags, final int computedHeartRate,
          final long heartBeatCount, BigDecimal heartBeatEventTime, final DataState dataState) {
        
        Log.i(TAG, String.format("rate: %3d, count: %3d, timestamp: %d, data state: %s",
            computedHeartRate, heartBeatCount, heartBeatEventTime.longValue(), dataState.toString()));
        
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
        
        if(dataState.equals(DataState.LIVE_DATA)) {
          try { //TODO async
            db.addHeartRateMeasurement(userProfile.getId(), computedHeartRate, heartBeatCount, heartBeatEventTime.longValue());
          }
          catch (DatabaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
    });
    
    heartRateDevice.subscribePage4AddtDataEvent(new IPage4AddtDataReceiver() {
      @Override
      public void onNewPage4AddtData(long estTimestamp, EnumSet<EventFlag> eventFlags, int manufacturerSpecificByte,
          BigDecimal previousHeartBeatEventTime) {

        Log.i(TAG, "Page4");
      }
    });

    heartRateDevice.subscribeCumulativeOperatingTimeEvent(new ICumulativeOperatingTimeReceiver() {
      @Override
      public void onNewCumulativeOperatingTime(long estTimestamp, EnumSet<EventFlag> eventFlags, long cumulativeOperatingTime) {
        Log.i(TAG, "operating time: " + cumulativeOperatingTime);
      }
    });

    heartRateDevice.subscribeManufacturerAndSerialEvent(new IManufacturerAndSerialReceiver() {
      @Override
      public void onNewManufacturerAndSerial(long estTimestamp, EnumSet<EventFlag> eventFlags, int manufacturerID, int serialNumber) {
        Log.i(TAG, String.format("manufacturer ID: %d, serial number: %d, timestamp: %d", manufacturerID, serialNumber, estTimestamp));
      }
    });

    heartRateDevice.subscribeVersionAndModelEvent(new IVersionAndModelReceiver() {
      @Override
      public void onNewVersionAndModel(long estTimestamp, EnumSet<EventFlag> eventFlags, int hardwareVersion,
          int softwareVersion, int modelNumber) {
        
        Log.i(TAG, String.format("HW version: %d, SW version: %d, model number: %d, timestamp: %d",
            hardwareVersion, softwareVersion, modelNumber, estTimestamp));
      }
    });

    heartRateDevice.subscribeCalculatedRrIntervalEvent(new ICalculatedRrIntervalReceiver() {
      @Override
      public void onNewCalculatedRrInterval(long estTimestamp, EnumSet<EventFlag> eventFlags, final BigDecimal calculatedRrInterval,
          RrFlag rrFlag) {
        
        Log.i(TAG, String.format("RR interval: %d, flag: %s, timestamp: %d",
            calculatedRrInterval.longValue(), rrFlag.toString(), estTimestamp));
        
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            tvRateInterval.setText(calculatedRrInterval.toString());
          }
        });
      }
    });
  }




  protected IPluginAccessResultReceiver<AntPlusHeartRatePcc> pluginAccessResultReceiver = new IPluginAccessResultReceiver<AntPlusHeartRatePcc>() {
    @Override
    public void onResultReceived(AntPlusHeartRatePcc result, RequestAccessResult resultCode, DeviceState initialDeviceState) {
      setStatus(R.string.searching_);
      Log.i(TAG, "search result: " + resultCode.toString());

      switch(resultCode){
        case SUCCESS:
          Log.i(TAG, "- device name: " + result.getDeviceName());
          Log.i(TAG, "- ANT device number: " + result.getAntDeviceNumber());
          Log.i(TAG, "- current device state: " + result.getCurrentDeviceState());
          
          setStatus(getString(R.string.watching_) + result.getDeviceName());
  
          heartRateDevice = result;
          subscribeToHrEvents();
          break;
          
        default:
          setStatus(getString(R.string.error_) + resultCode.toString());
          break;
      }
    }
  };

  //Receives state changes and shows it on the status display line
  protected  IDeviceStateChangeReceiver deviceStateChangeReceiver = new IDeviceStateChangeReceiver() {
    @Override
    public void onDeviceStateChange(final DeviceState newDeviceState) {
      Log.i(TAG, "Device state changed!");
      Log.i(TAG, "- device name: " + heartRateDevice.getDeviceName());
      Log.i(TAG, "- new device state: " + newDeviceState);
    }
  };
}
