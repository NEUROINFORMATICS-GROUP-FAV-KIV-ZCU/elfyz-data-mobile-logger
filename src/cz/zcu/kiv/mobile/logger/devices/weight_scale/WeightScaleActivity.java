package cz.zcu.kiv.mobile.logger.devices.weight_scale;

import java.math.BigDecimal;
import java.util.EnumSet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.AdvancedMeasurement;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.BodyWeightStatus;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.IAdvancedMeasurementFinishedReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.IBasicMeasurementFinishedReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.IBodyWeightBroadcastReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.UserProfile;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.WeightScaleRequestStatus;
import com.dsi.ant.plugins.antplus.pcc.defines.BatteryStatus;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IBatteryStatusReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IManufacturerIdentificationReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IManufacturerSpecificDataReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IProductInformationReceiver;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.commands.AInsertMeasurementCommand;
import cz.zcu.kiv.mobile.logger.data.database.commands.AInsertMeasurementCommand.InsertCommandListener;
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertWeightScaleAdvancedMeasurementCommand;
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertWeightScaleBasicMeasurementCommand;
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertWeightScaleBatteryStatusCommand;
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertWeightScaleManufacturerIdentificationCommand;
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertWeightScaleManufacturerSpecificDataCommand;
import cz.zcu.kiv.mobile.logger.data.database.commands.InsertWeightScaleProductInformationTableCommand;
import cz.zcu.kiv.mobile.logger.data.types.Gender;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public class WeightScaleActivity extends Activity implements InsertCommandListener {
  private static final String TAG = WeightScaleActivity.class.getSimpleName();
  
  private static final String UNIT_KG = " kg";
  private static final String UNIT_KCAL = " kcal";
  private static final String UNIT_PERCENT = " %";
  
  protected TextView tvStatus;
  protected TextView tvBodyWeight;
  protected TextView tvDataStatus;
  protected TextView tvHydrationPercentage;
  protected TextView tvFatPercentage;
  protected TextView tvMuscleMass;
  protected TextView tvBoneMass;
  protected TextView tvActiveMetabolicRate;
  protected TextView tvBasalMetabolicRate;
  
  protected PccReleaseHandle<AntPlusWeightScalePcc> releaseHandle;
  protected AntPlusWeightScalePcc weightScaleDevice;
  protected UserProfile userProfileANT;
  protected Profile userProfile;
  
  protected WeightScaleMeasurementTable dbWSM;

  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_weight_scale);
    
    tvStatus = (TextView) findViewById(R.id.tv_status);
    tvBodyWeight = (TextView) findViewById(R.id.tv_body_weight);
    tvDataStatus = (TextView) findViewById(R.id.tv_data_status);
    tvHydrationPercentage = (TextView) findViewById(R.id.tv_hydration_percentage);
    tvFatPercentage = (TextView) findViewById(R.id.tv_fat_percentage);
    tvMuscleMass = (TextView) findViewById(R.id.tv_muscle_mass);
    tvBoneMass = (TextView) findViewById(R.id.tv_bone_mass);
    tvActiveMetabolicRate = (TextView) findViewById(R.id.tv_active_metabolic_rate);
    tvBasalMetabolicRate = (TextView) findViewById(R.id.tv_basal_metabolic_rate);
    
    userProfile = Application.getInstance().getUserProfileOrLogIn();
    
    if(userProfile == null){
      AndroidUtils.toast(this, R.string.alert_must_be_logged_in);
      Log.e(TAG, "User must be logged in.");
      finish();
      return;
    }
    
    dbWSM = Application.getInstance().getDatabase().getWeightScaleMeasurementTable();
    
    userProfileANT = getAntProfile(userProfile);
    
    releaseHandle = AntPlusWeightScalePcc.requestAccess(this, this, pluginAccessResultReceiver, deviceStateChangeReceiver);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.weight_scale, menu);
    return true;
  }
  
  public void showAllRecords(View view) {
    startActivity(new Intent(this, WeightScaleDataListActivity.class));
  }
  
  public void doBasicMeasurement(View view){//TODO disable if device not available
    boolean success = weightScaleDevice.requestBasicMeasurement(new IBasicMeasurementFinishedReceiver() {
      @Override
      public void onBasicMeasurementFinished(long estTimestamp, EnumSet<EventFlag> eventFlags,
          final WeightScaleRequestStatus status, final BigDecimal bodyWeight) {
        WeightScaleBasicMeasurement data = new WeightScaleBasicMeasurement(estTimestamp, eventFlags, status, bodyWeight, false);
        
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            setText(tvDataStatus, status.toString());

            if(status == WeightScaleRequestStatus.SUCCESS){
              setText(tvBodyWeight, status == WeightScaleRequestStatus.SUCCESS, bodyWeight.toString() + UNIT_KG);
              setText(tvStatus, R.string.weight_scale_measuring_ok);
            }
            else {
              setText(tvStatus, R.string.weight_scale_measuring_failed);
            }
          }
        });

        new InsertWeightScaleBasicMeasurementCommand(userProfile.getId(), data, WeightScaleActivity.this).execute();
      }
    });
    
    if(!success){
      setStatus(R.string.weight_scale_measuring_failed);
    }
  }
  
  public void doAdvancedMeasurement(View view){
    boolean success = weightScaleDevice.requestAdvancedMeasurement(new IAdvancedMeasurementFinishedReceiver() {
      @Override
      public void onAdvancedMeasurementFinished(long estTimestamp, EnumSet<EventFlag> eventFlags,
          final WeightScaleRequestStatus status, final AdvancedMeasurement measurement) { //TODO custom enums with mapping...?
        WeightScaleAdvancedMeasurement data = new WeightScaleAdvancedMeasurement(estTimestamp, eventFlags, status,
            measurement.activeMetabolicRate, measurement.basalMetabolicRate, measurement.bodyFatPercentage, measurement.bodyWeight,
            measurement.boneMass, measurement.hydrationPercentage, measurement.muscleMass, false);
        
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            setText(tvDataStatus, status.toString());

            if(status == WeightScaleRequestStatus.SUCCESS){
              setText(tvBodyWeight, measurement.bodyWeight.intValue() != -1, measurement.bodyWeight.toString() + UNIT_KG);
              setText(tvHydrationPercentage, measurement.hydrationPercentage.intValue() != -1, measurement.hydrationPercentage.toString() + UNIT_PERCENT);
              setText(tvFatPercentage, measurement.bodyFatPercentage.intValue() != -1, measurement.bodyFatPercentage.toString() + UNIT_KG);
              setText(tvMuscleMass, measurement.muscleMass.intValue() != -1, measurement.muscleMass.toString() + UNIT_KG);
              setText(tvBoneMass, measurement.boneMass.intValue() != -1, measurement.boneMass.toString() + UNIT_KG);
              setText(tvActiveMetabolicRate, measurement.activeMetabolicRate.intValue() != -1, measurement.activeMetabolicRate.toString() + UNIT_KCAL);
              setText(tvBasalMetabolicRate, measurement.basalMetabolicRate.intValue() != -1, measurement.basalMetabolicRate.toString() + UNIT_KCAL);
              setText(tvStatus, R.string.weight_scale_measure_ok);
            }
            else {
              setText(tvStatus, R.string.weight_scale_measuring_failed);
            }
          }
        });
        
        new InsertWeightScaleAdvancedMeasurementCommand(userProfile.getId(), data, WeightScaleActivity.this).execute();
      }
    },
    userProfileANT);
    
    if(!success){
      setStatus(R.string.weight_scale_measuring_failed);
    }
  }

  protected void subscribeToWsEvents() {
    Log.i(TAG, "subscribing to events...");

    weightScaleDevice.subscribeBatteryStatusEvent(new IBatteryStatusReceiver() {
      @Override
      public void onNewBatteryStatus(long estTimestamp, EnumSet<EventFlag> eventFlags, long cumulativeOperatingTime,
          BigDecimal batteryVoltage, BatteryStatus batteryStatus, int cumulativeOperatingTimeResolution,
          int numberOfBatteries, int batteryIdentifier) {
        
        WeightScaleBatteryStatus data = new WeightScaleBatteryStatus(estTimestamp, eventFlags, cumulativeOperatingTime,
            batteryVoltage, batteryStatus, cumulativeOperatingTimeResolution, numberOfBatteries, batteryIdentifier, false);
        new InsertWeightScaleBatteryStatusCommand(userProfile.getId(), data, WeightScaleActivity.this).execute();
      }
    });
    
    weightScaleDevice.subscribeManufacturerIdentificationEvent(new IManufacturerIdentificationReceiver() {
      @Override
      public void onNewManufacturerIdentification(long estTimestamp, EnumSet<EventFlag> eventFlags, int hardwareRevision,
          int manufacturerID, int modelNumber) {

        WeightScaleManufacturerIdentification data = new WeightScaleManufacturerIdentification(estTimestamp, eventFlags,
            hardwareRevision, manufacturerID, modelNumber, false);
        new InsertWeightScaleManufacturerIdentificationCommand(userProfile.getId(), data, WeightScaleActivity.this).execute();
      }
    });
    
    weightScaleDevice.subscribeManufacturerSpecificDataEvent(new IManufacturerSpecificDataReceiver() {
      @Override
      public void onNewManufacturerSpecificData(long estTimestamp, EnumSet<EventFlag> eventFlags, byte[] rawDataBytes) {
        WeightScaleManufacturerSpecificData data = new WeightScaleManufacturerSpecificData(estTimestamp, eventFlags, rawDataBytes, false);
        new InsertWeightScaleManufacturerSpecificDataCommand(userProfile.getId(), data, WeightScaleActivity.this).execute();
      }
    });
    
    weightScaleDevice.subscribeProductInformationEvent(new IProductInformationReceiver() {
      @Override
      public void onNewProductInformation(long estTimestamp, EnumSet<EventFlag> eventFlags, int mainSoftwareRevision,
          int supplementalSoftwareRevision, long serialNumber) {
        WeightScaleProductInformation data = new WeightScaleProductInformation(estTimestamp, eventFlags,
            mainSoftwareRevision, supplementalSoftwareRevision, serialNumber, false);
        new InsertWeightScaleProductInformationTableCommand(userProfile.getId(), data, WeightScaleActivity.this).execute();
      }
    });
    
    weightScaleDevice.subscribeBodyWeightBroadcastEvent(new IBodyWeightBroadcastReceiver() {
      @Override
      public void onNewBodyWeightBroadcast(long estTimestamp, EnumSet<EventFlag> eventFlags,
          final BodyWeightStatus bodyWeightStatus, final BigDecimal bodyWeight) {

        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            tvBodyWeight.setText(
              (bodyWeightStatus == BodyWeightStatus.VALID)
                ? bodyWeight.toString() + " kg"
                : getString(R.string.value_n_a)
            );
            tvDataStatus.setText(bodyWeightStatus.toString());
          }
        });
      }
    });
  }

  
  protected IPluginAccessResultReceiver<AntPlusWeightScalePcc> pluginAccessResultReceiver = new IPluginAccessResultReceiver<AntPlusWeightScalePcc>() {
    
    @Override
    public void onResultReceived(AntPlusWeightScalePcc result, RequestAccessResult resultCode, DeviceState initialDeviceState) {
      Log.d(TAG, "search result: " + resultCode.toString());
      
      switch (resultCode) {
        case SUCCESS:
          Log.i(TAG, "- device name: " + result.getDeviceName());
          Log.i(TAG, "- ANT device number: " + result.getAntDeviceNumber());
          Log.i(TAG, "- current device state: " + result.getCurrentDeviceState());
          
          setStatus(getString(R.string.watching_) + result.getDeviceName());
          
          weightScaleDevice = result;
          subscribeToWsEvents();
          break;
  
        default:
          setStatus(getString(R.string.error_) + resultCode.toString());
          break;
      }
    }
  };
  
  protected  IDeviceStateChangeReceiver deviceStateChangeReceiver = new IDeviceStateChangeReceiver() {
    @Override
    public void onDeviceStateChange(final DeviceState newDeviceState) {
      Log.i(TAG, "Device state changed!");
      Log.i(TAG, "- device name: " + weightScaleDevice.getDeviceName());
      Log.i(TAG, "- new device state: " + newDeviceState);
    }
  };

  private UserProfile getAntProfile(Profile userProfile) {
    UserProfile profile = new UserProfile();
    profile.age = userProfile.calculateAge();
    profile.gender = userProfile.getGender() == Gender.MALE ? AntPlusWeightScalePcc.Gender.MALE : AntPlusWeightScalePcc.Gender.FEMALE;
    profile.height = userProfile.getHeight();
    profile.activityLevel = userProfile.getActivityLevel();
    profile.lifetimeAthlete = userProfile.isLifetimeAthlete();
    return profile;
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
  
  protected void setText(TextView textView, int stringID) {
    setText(textView, getString(stringID));
  }
  
  protected void setText(TextView textView, CharSequence text) {
    textView.setText(text);
  }
  
  protected void setText(TextView textView, boolean condition, String positiveValue) {
    setText(textView, condition, positiveValue, R.string.value_n_a);
  }
  
  protected void setText(TextView textView, boolean condition, String positiveValue, int negativeValueID) {
    setText(textView, condition, positiveValue, getString(negativeValueID));
  }
  
  protected void setText(TextView textView, boolean condition, String positiveValue, String negativeValue) {
    textView.setText(
        condition
          ? positiveValue
          : negativeValue
    );
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

  @Override
  public void onInsertCommandFinished(AInsertMeasurementCommand<?> command, AsyncTaskResult<Long> result) {
    if(result.getError() != null) {
      Log.e(TAG, "Failed to insert record to DB.", result.getError());
      AndroidUtils.toast(this, R.string.fail_db_insert);
    }
  }
}
