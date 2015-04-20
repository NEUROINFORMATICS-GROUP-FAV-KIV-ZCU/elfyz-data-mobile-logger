package cz.zcu.kiv.mobile.logger.service.communicators.heart_rate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

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
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.data.database.commands.AInsertMeasurementCommand;
import cz.zcu.kiv.mobile.logger.data.database.commands.AInsertMeasurementCommand.InsertCommandListener;
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertHeartRateCalculatedRrIntervalCommand;
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertHeartRateCumulativeOperatingTimeCommand;
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertHeartRateManufacturerAndSerialCommand;
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertHeartRateMeasurementCommand;
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertHeartRatePage4Command;
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertHeartRateVersionAndModelCommand;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.data.types.heart_rate.HeartRateCalculatedRrInterval;
import cz.zcu.kiv.mobile.logger.data.types.heart_rate.HeartRateCumulativeOperatingTime;
import cz.zcu.kiv.mobile.logger.data.types.heart_rate.HeartRateManufacturerAndSerial;
import cz.zcu.kiv.mobile.logger.data.types.heart_rate.HeartRateMeasurement;
import cz.zcu.kiv.mobile.logger.data.types.heart_rate.HeartRatePage4;
import cz.zcu.kiv.mobile.logger.data.types.heart_rate.HeartRateVersionAndModel;
import cz.zcu.kiv.mobile.logger.service.communicators.ACommunicator;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public class HeartRateCommunicator extends ACommunicator implements InsertCommandListener {
  private static final String TAG = HeartRateCommunicator.class.getSimpleName();
  
  protected PccReleaseHandle<AntPlusHeartRatePcc> heartRateReleaseHandle;
  protected AntPlusHeartRatePcc heartRateDevice;

  protected Collection<HeartRateListener> listeners;
  
  protected Profile userProfile;
  
  
  public HeartRateCommunicator(Context context) {
    super(context);
    listeners = new HashSet<HeartRateCommunicator.HeartRateListener>();
    userProfile = Application.getInstance().getUserProfileOrLogIn();
  }

  
  public void startListening(Activity caller, HeartRateListener listener) {
    if(listener != null)
      listeners.add(listener);
    
    if(heartRateReleaseHandle == null)
      startHeartRate(caller);
  }
  
  public void stopListening(HeartRateListener listener) {
    if(listener != null)
      listeners.remove(listener);
  }
  
  public String getConnectedDeviceName() {
    return heartRateDevice != null
        ? heartRateDevice.getDeviceName()
        : null;
  }
  

  protected void startHeartRate(Activity caller) {
    heartRateReleaseHandle = AntPlusHeartRatePcc.requestAccess(
        caller,
        context,
        new IPluginAccessResultReceiver<AntPlusHeartRatePcc>() {
          @Override
          public void onResultReceived(AntPlusHeartRatePcc result, RequestAccessResult resultCode, DeviceState initialDeviceState) {
            Log.i(TAG, "Heart rate search result: " + resultCode.toString());

            switch(resultCode) {
              //success
              case SUCCESS:
                Log.i(TAG, "- device name: " + result.getDeviceName());
                Log.i(TAG, "- ANT device number: " + result.getAntDeviceNumber());
                Log.i(TAG, "- current device state: " + result.getCurrentDeviceState());
                
                for (HeartRateListener listener : listeners) {
                  listener.onDeviceConnected(result.getDeviceName());
                }
                
                heartRateDevice = result;
                subscribeToHrEvents();
                return;
                
              //failures
              case DEPENDENCY_NOT_INSTALLED:
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(R.string.missing_dependency);
                alert.setMessage(context.getString(R.string.required_app_) + "\n\"" + AntPlusHeartRatePcc.getMissingDependencyName() + "\n\n" + context.getString(R.string.ant_plus_needed_go_to_store));
                alert.setCancelable(true);
                alert.setPositiveButton(context.getString(R.string.go_to_play_store), new OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    Intent startStore = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=" + AntPlusHeartRatePcc.getMissingDependencyPackageName()));
                      startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(startStore);
                  }
                });
                alert.setNegativeButton(context.getString(R.string.dialog_cancel_button), new OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                  }
                });

                alert.create().show();
                //no break to notice the listeners
                
              default:
                for (HeartRateListener listener : listeners) {
                  listener.onConnectionError(resultCode);
                }
                break;
            }
            
            stopHeartRate();
          }
        },
        new IDeviceStateChangeReceiver() {
          @Override
          public void onDeviceStateChange(DeviceState state) {
            for (HeartRateListener listener : listeners) {
              listener.onDeviceStateChange(state);
            }
          }
        }
      );
  }
  
  protected void stopHeartRate() {
    CloseUtils.close(heartRateReleaseHandle);
    heartRateDevice = null;
    heartRateReleaseHandle = null;
  }
  
  protected void subscribeToHrEvents() {
    heartRateDevice.subscribeHeartRateDataEvent(new IHeartRateDataReceiver() {
      @Override
      public void onNewHeartRateData(long estTimestamp, EnumSet<EventFlag> eventFlags, final int computedHeartRate,
          final long heartBeatCount, BigDecimal heartBeatEventTime, final DataState dataState) {
        
        HeartRateMeasurement measurement = new HeartRateMeasurement(estTimestamp, eventFlags, computedHeartRate,
            heartBeatCount, heartBeatEventTime, dataState, false);

        for (HeartRateListener listener : listeners) {
          listener.onHeartRateDataReceived(measurement);
        }
        
        new InsertHeartRateMeasurementCommand(userProfile.getId(), measurement, HeartRateCommunicator.this).execute();
      }
    });
    
    heartRateDevice.subscribePage4AddtDataEvent(new IPage4AddtDataReceiver() {
      @Override
      public void onNewPage4AddtData(long estTimestamp, EnumSet<EventFlag> eventFlags, int manufacturerSpecificByte,
          BigDecimal previousHeartBeatEventTime) {

        HeartRatePage4 data = new HeartRatePage4(estTimestamp, eventFlags, manufacturerSpecificByte, previousHeartBeatEventTime, false);
        
        for (HeartRateListener listener : listeners) {
          listener.onAdditionalDataReceived(data);
        }
        
        new InsertHeartRatePage4Command(userProfile.getId(), data, HeartRateCommunicator.this).execute();
      }
    });

    heartRateDevice.subscribeCumulativeOperatingTimeEvent(new ICumulativeOperatingTimeReceiver() {
      @Override
      public void onNewCumulativeOperatingTime(long estTimestamp, EnumSet<EventFlag> eventFlags, long cumulativeOperatingTime) {
        HeartRateCumulativeOperatingTime data = new HeartRateCumulativeOperatingTime(estTimestamp, eventFlags, cumulativeOperatingTime, false);
        
        for (HeartRateListener listener : listeners) {
          listener.onCumulativeOperatingTimeReceived(data);
        }
        
        new InsertHeartRateCumulativeOperatingTimeCommand(userProfile.getId(), data, HeartRateCommunicator.this).execute();
      }
    });

    heartRateDevice.subscribeManufacturerAndSerialEvent(new IManufacturerAndSerialReceiver() {
      @Override
      public void onNewManufacturerAndSerial(long estTimestamp, EnumSet<EventFlag> eventFlags, int manufacturerID, int serialNumber) {
        HeartRateManufacturerAndSerial data = new HeartRateManufacturerAndSerial(estTimestamp, eventFlags, manufacturerID, serialNumber, false);
        
        for (HeartRateListener listener : listeners) {
          listener.onManufacturerAndSerialReceived(data);
        }
        
        new InsertHeartRateManufacturerAndSerialCommand(userProfile.getId(), data, HeartRateCommunicator.this).execute();
      }
    });

    heartRateDevice.subscribeVersionAndModelEvent(new IVersionAndModelReceiver() {
      @Override
      public void onNewVersionAndModel(long estTimestamp, EnumSet<EventFlag> eventFlags, int hardwareVersion,
          int softwareVersion, int modelNumber) {
        HeartRateVersionAndModel data = new HeartRateVersionAndModel(estTimestamp, eventFlags, hardwareVersion, softwareVersion, modelNumber, false);
        
        for (HeartRateListener listener : listeners) {
          listener.onVersionAndModelReceived(data);
        }
        
        new InsertHeartRateVersionAndModelCommand(userProfile.getId(), data, HeartRateCommunicator.this).execute();
      }
    });

    heartRateDevice.subscribeCalculatedRrIntervalEvent(new ICalculatedRrIntervalReceiver() {
      @Override
      public void onNewCalculatedRrInterval(long estTimestamp, EnumSet<EventFlag> eventFlags,
          final BigDecimal calculatedRrInterval, RrFlag rrFlag) {
        HeartRateCalculatedRrInterval data = new HeartRateCalculatedRrInterval(estTimestamp, eventFlags, calculatedRrInterval, rrFlag, false);
        
        for (HeartRateListener listener : listeners) {
          listener.onCalculatedRrIntervalReceived(data);
        }
        
        new InsertHeartRateCalculatedRrIntervalCommand(userProfile.getId(), data, HeartRateCommunicator.this).execute();
      }
    });
  }
  
  
  @Override
  public void onInsertCommandFinished(AInsertMeasurementCommand<?> command, AsyncTaskResult<Long> result) {
    if(result.getError() != null) {
      Log.e(TAG, "Failed to insert record to DB.", result.getError());
      AndroidUtils.toast(context, R.string.fail_db_insert);
    }
  }
  
  
  public void close() {
    stopHeartRate();
    
    for (HeartRateListener listener : listeners) {
      listener.onConnectionClosed();
    }
    
    listeners.clear();
    super.close();
  }
  


  /**
   * Listener for heart rate sensor events.
   */
  public static interface HeartRateListener {
    /**
     * When HR device was requested and the connection is made after this listener has been registered.
     * If Device has been already connected and new listener is registered afterwards, this method is not called.
     * @param deviceName Name of connected device.
     */
    void onDeviceConnected(String deviceName);
    /**
     * When state of device connection is changed.
     * @param state New state.
     */
    void onDeviceStateChange(DeviceState state);
    /**
     * When HR device was requested and the connection couldn't be established.
     * @param resultCode Result code explaining the failure.
     */
    void onConnectionError(RequestAccessResult resultCode);
    /**
     * When connection termination was requested. No more calls to this listener are made after this call.
     * Listener is automatically unregistered and can be used for another register.
     */
    void onConnectionClosed();
    
    void onHeartRateDataReceived(HeartRateMeasurement measurement);
    void onAdditionalDataReceived(HeartRatePage4 data);
    void onCumulativeOperatingTimeReceived(HeartRateCumulativeOperatingTime data);
    void onManufacturerAndSerialReceived(HeartRateManufacturerAndSerial data);
    void onVersionAndModelReceived(HeartRateVersionAndModel data);
    void onCalculatedRrIntervalReceived(HeartRateCalculatedRrInterval data);
  }
}
