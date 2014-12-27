package cz.zcu.kiv.mobile.logger.services;

import java.io.Closeable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;

import android.app.Activity;
import android.app.AlertDialog;
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

import cz.zcu.kiv.mobile.logger.utils.CloseUtil;


public class HeartRateCommunicator implements Closeable {
  private static final String TAG = HeartRateCommunicator.class.getSimpleName();
  
  protected DeviceCommunicatorService service;
  
  protected PccReleaseHandle<AntPlusHeartRatePcc> heartRateReleaseHandle;
  protected AntPlusHeartRatePcc heartRateDevice;

  protected Collection<HeartRateListener> listeners;
  
  
  public HeartRateCommunicator(DeviceCommunicatorService service) {
    this.service = service;
    listeners = new HashSet<HeartRateCommunicator.HeartRateListener>();
  }

  
  public void startListening(Activity caller, HeartRateListener listener) { //TODO handle passive not null -> no data for some time (till connection is made by ant plus)
    listeners.add(listener);
    
    if(heartRateReleaseHandle == null)
      startHeartRate(caller);
  }
  
  public void stopListening(HeartRateListener listener) {
    listeners.remove(listener);
    
//    if(listeners.isEmpty()) TODO 
//      stopHeartRate();
  }
  

  protected void startHeartRate(final Activity caller) {
    heartRateReleaseHandle = AntPlusHeartRatePcc.requestAccess(
        caller,
        service,
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
                  listener.onDeviceConnected(result.getAntDeviceNumber(), result.getDeviceName());
                }
                
                heartRateDevice = result;
                subscribeToHrEvents();
                return;
                
              //failures
              case DEPENDENCY_NOT_INSTALLED:
                AlertDialog.Builder alert = new AlertDialog.Builder(service);
                alert.setTitle("Missing Dependency");
                alert.setMessage("The required service\n\"" + AntPlusHeartRatePcc.getMissingDependencyName() + "\"\n was not found. You need to install the ANT+ Plugins service or you may need to update your existing version if you already have it. Do you want to launch the Play Store to get it?");
                alert.setCancelable(true);
                alert.setPositiveButton("Go to Store", new OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    Intent startStore = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=" + AntPlusHeartRatePcc.getMissingDependencyPackageName()));
                      startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    service.startActivity(startStore);
                  }
                });
                alert.setNegativeButton("Cancel", new OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                  }
                });

                alert.create().show();
                //no brake to notice the listeners
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
        });
  }
  
  protected void stopHeartRate() {
    CloseUtil.close(heartRateReleaseHandle);
    heartRateDevice = null;
    heartRateReleaseHandle = null;
  }
  
//TODO async db všude
  protected void subscribeToHrEvents() {
    heartRateDevice.subscribeHeartRateDataEvent(new IHeartRateDataReceiver() {
      @Override
      public void onNewHeartRateData(long estTimestamp, EnumSet<EventFlag> eventFlags, final int computedHeartRate,
          final long heartBeatCount, BigDecimal heartBeatEventTime, final DataState dataState) {
        
        for (HeartRateListener listener : listeners) {
          listener.onHeartRateDataReceived(estTimestamp, eventFlags, computedHeartRate,
          heartBeatCount, heartBeatEventTime, dataState);
        }

      }
    });
    
    heartRateDevice.subscribePage4AddtDataEvent(new IPage4AddtDataReceiver() {
      @Override
      public void onNewPage4AddtData(long estTimestamp, EnumSet<EventFlag> eventFlags, int manufacturerSpecificByte,
          BigDecimal previousHeartBeatEventTime) {

        for (HeartRateListener listener : listeners) {
          listener.onAdditionalDataReceived(estTimestamp, eventFlags, manufacturerSpecificByte, previousHeartBeatEventTime);
        }
      }
    });

    heartRateDevice.subscribeCumulativeOperatingTimeEvent(new ICumulativeOperatingTimeReceiver() {
      @Override
      public void onNewCumulativeOperatingTime(long estTimestamp, EnumSet<EventFlag> eventFlags, long cumulativeOperatingTime) {
        for (HeartRateListener listener : listeners) {
          listener.onCumulativeOperatingTimeReceived(estTimestamp, eventFlags, cumulativeOperatingTime);
        }
      }
    });

    heartRateDevice.subscribeManufacturerAndSerialEvent(new IManufacturerAndSerialReceiver() {
      @Override
      public void onNewManufacturerAndSerial(long estTimestamp, EnumSet<EventFlag> eventFlags, int manufacturerID, int serialNumber) {
        for (HeartRateListener listener : listeners) {
          listener.onManufacturerAndSerialReceived(estTimestamp, eventFlags, manufacturerID, serialNumber);
        }
      }
    });

    heartRateDevice.subscribeVersionAndModelEvent(new IVersionAndModelReceiver() {
      @Override
      public void onNewVersionAndModel(long estTimestamp, EnumSet<EventFlag> eventFlags, int hardwareVersion,
          int softwareVersion, int modelNumber) {
        for (HeartRateListener listener : listeners) {
          listener.onVersionAndModelReceived(estTimestamp, eventFlags, hardwareVersion, softwareVersion, modelNumber);
        }
      }
    });

    heartRateDevice.subscribeCalculatedRrIntervalEvent(new ICalculatedRrIntervalReceiver() {
      @Override
      public void onNewCalculatedRrInterval(long estTimestamp, EnumSet<EventFlag> eventFlags,
          final BigDecimal calculatedRrInterval, RrFlag rrFlag) {
        for (HeartRateListener listener : listeners) {
          listener.onCalculatedRrIntervalReceived(estTimestamp, eventFlags, calculatedRrInterval, rrFlag);
        }
      }
    });
  }
  
  
  public void close() {
    stopHeartRate();
    listeners.clear();
    //TODO send message to remaining listeners?
  }
  


  public static interface HeartRateListener {
    void onDeviceConnected(int antDeviceNumber, String deviceName);
    void onDeviceStateChange(DeviceState state);
    void onConnectionError(RequestAccessResult resultCode);
    
    void onHeartRateDataReceived(long estTimestamp, EnumSet<EventFlag> eventFlags, int computedHeartRate,
        long heartBeatCount, BigDecimal heartBeatEventTime, DataState dataState);
    void onAdditionalDataReceived(long estTimestamp, EnumSet<EventFlag> eventFlags, int manufacturerSpecificByte,
        BigDecimal previousHeartBeatEventTime);
    void onCumulativeOperatingTimeReceived(long estTimestamp, EnumSet<EventFlag> eventFlags, long cumulativeOperatingTime);
    void onManufacturerAndSerialReceived(long estTimestamp, EnumSet<EventFlag> eventFlags, int manufacturerID, int serialNumber);
    void onVersionAndModelReceived(long estTimestamp, EnumSet<EventFlag> eventFlags, int hardwareVersion,
        int softwareVersion, int modelNumber);
    void onCalculatedRrIntervalReceived(long estTimestamp, EnumSet<EventFlag> eventFlags, BigDecimal calculatedRrInterval,
        RrFlag rrFlag);
  }
}
