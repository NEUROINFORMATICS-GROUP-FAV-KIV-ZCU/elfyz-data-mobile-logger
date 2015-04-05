package cz.zcu.kiv.mobile.logger;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.RrFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;

import cz.zcu.kiv.mobile.logger.bluetooth.picker.BluetoothDevicePickerActivity;
import cz.zcu.kiv.mobile.logger.data.database.BloodPressureMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.Database;
import cz.zcu.kiv.mobile.logger.data.database.GlucoseMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateCalculatedRrIntervalTable;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateCumulativeOperatingTimeTable;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateManufacturerAndSerialTable;
import cz.zcu.kiv.mobile.logger.data.database.ProfileTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.EntryNotFoundException;
import cz.zcu.kiv.mobile.logger.data.types.Gender;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure.BloodPressureMeasurement;
import cz.zcu.kiv.mobile.logger.devices.fora.glucose.GlucoseMeasurement;
import cz.zcu.kiv.mobile.logger.devices.fora.glucose.GlucoseMeterDeviceCommunicatorTask;
import cz.zcu.kiv.mobile.logger.devices.fora.glucose.GlucoseMeterDeviceCommunicatorTask.GlucoseMeterDeviceListener;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateCalculatedRrInterval;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateCumulativeOperatingTime;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateDataListActivity;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateManufacturerAndSerial;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.ExperimentParametersData;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.GenericParameterData;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.GenericParameterDataList;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.ParameterAttributeData;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.ParameterAttributeDataList;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;

//XXX just for developer testing
public class TestActivity extends Activity implements GlucoseMeterDeviceListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);

    Database db = ((Application) getApplication()).getDatabase();
    
    try {
      Profile profile = null;
      try {
        profile = db.getProfileTable().getProfile(1L);
      }
      catch(EntryNotFoundException e) {
        db.getProfileTable().createProfile(profile = getTestProfile());
      }
      
      Application.getInstance().setUserProfile(profile);
      
//      addData(db, profile.getId());
          
//      System.out.println("\nSTARTING TEST ACTIVITY\n");
//      startActivity(testIntent);
//  
//      finish();
      
      
//      Intent testIntent = new Intent(this, HeartRateDataListActivity.class);
//      testIntent.putExtra(UploadGenericParametersActivity.PARAM_GENERIC_PARAMETERS, getParameters());
//      startActivityForResult(testIntent, 1111);
    }
    catch (DatabaseException e) {
      e.printStackTrace();
    }
  }

  private void addData(Database db, long userID) {
      try {
        BloodPressureMeasurementTable t = db.getBloodPressureMeasurementTable();
        t.addMeasurement(userID, new BloodPressureMeasurement(new GregorianCalendar(2015, 1, 2, 15, 30),  80, 60, 70, 50, false));
        t.addMeasurement(userID, new BloodPressureMeasurement(new GregorianCalendar(2015, 1, 2, 15, 42),  90, 60, 75, 60, false));
        t.addMeasurement(userID, new BloodPressureMeasurement(new GregorianCalendar(2015, 1, 2, 16, 10),  100, 70, 85, 70, false));
        t.addMeasurement(userID, new BloodPressureMeasurement(new GregorianCalendar(2015, 1, 2, 17, 53),  80, 60, 70, 52, false));
      }
      catch (DatabaseException e) {
        e.printStackTrace();
      }
      try {
        GlucoseMeasurementTable t = db.getGlucoseMeasurementTable();
        t.addMeasurement(userID, new GlucoseMeasurement(new GregorianCalendar(2015, 1, 3, 8, 11), 147, 6, 0, 0, false));
        t.addMeasurement(userID, new GlucoseMeasurement(new GregorianCalendar(2015, 1, 3, 8, 11), 140, 5, 0, 0, false));
        t.addMeasurement(userID, new GlucoseMeasurement(new GregorianCalendar(2015, 1, 3, 8, 11), 155, 7, 0, 0, false));
        t.addMeasurement(userID, new GlucoseMeasurement(new GregorianCalendar(2015, 1, 3, 8, 11), 130, 2, 0, 0, false));
      }
      catch (DatabaseException e) {
        e.printStackTrace();
      }
      try {
        HeartRateCalculatedRrIntervalTable t = db.getHeartRateCalculatedRrIntervalTable();
        t.addCalculatedRrIntervalData(userID, new HeartRateCalculatedRrInterval(111111L, null, new BigDecimal(1.1), RrFlag.HEART_RATE_ZERO_DETECTED, false));
        t.addCalculatedRrIntervalData(userID, new HeartRateCalculatedRrInterval(111200L, null, new BigDecimal(1.0), RrFlag.DATA_SOURCE_PAGE_4, false));
        t.addCalculatedRrIntervalData(userID, new HeartRateCalculatedRrInterval(111500L, null, new BigDecimal(1.2), RrFlag.DATA_SOURCE_CACHED, false));
        t.addCalculatedRrIntervalData(userID, new HeartRateCalculatedRrInterval(111650L, null, new BigDecimal(10.9), RrFlag.UNRECOGNIZED, false));
      }
      catch (DatabaseException e) {
        e.printStackTrace();
      }
      try {
        HeartRateCumulativeOperatingTimeTable t = db.getHeartRateCumulativeOperatingTimeTable();
        t.addCumulativeOperatingTime(userID, new HeartRateCumulativeOperatingTime(222222L, null, 10, false));
        t.addCumulativeOperatingTime(userID, new HeartRateCumulativeOperatingTime(222300L, null, 15, false));
        t.addCumulativeOperatingTime(userID, new HeartRateCumulativeOperatingTime(222430L, null, 20, false));
        t.addCumulativeOperatingTime(userID, new HeartRateCumulativeOperatingTime(222600L, null, 25, false));
      }
      catch (DatabaseException e) {
        e.printStackTrace();
      }
      HeartRateManufacturerAndSerialTable t = db.getHeartRateManufacturerAndSerialTable();
//      t.addManufacturerAndSerial(userID, new HeartRateManufacturerAndSerial(333333L, null, 1, 2, false));
//      t.add
  }

  private Profile getTestProfile() {
    return new Profile(1L,
        "Honza",
        "krupa@students.zcu.cz",
        "Jan", "Krupicka",
        new GregorianCalendar(1989, 12, 30),
        Gender.MALE, 185, 3, false);
  }

  private void printProfiles(Database db) {
    try {
      Cursor c = db.getProfileTable().getProfileNames();
      int idi = c.getColumnIndex(ProfileTable.COLUMN_ID);
      int ni = c.getColumnIndex(ProfileTable.COLUMN_NAME);
      
      for(c.moveToFirst(); c.isAfterLast(); c.moveToNext()) {
        System.out.println(c.getString(idi) + " - " + c.getString(ni));
      }
    }
    catch (DatabaseException e) {
      e.printStackTrace();
    }
    
  }

  private Parcelable getParameters() {
    ExperimentParametersData data = new ExperimentParametersData(false,
      new GenericParameterDataList(
        Arrays.asList(
          new GenericParameterData("par1", "parValue1",
            new ParameterAttributeDataList(
              Arrays.asList(
                new ParameterAttributeData("att1", "value1"),
                  new ParameterAttributeData("att2", "value2")
                ))
              ),
          new GenericParameterData("par2", "parValue2",
            new ParameterAttributeDataList(
              Arrays.asList(
                new ParameterAttributeData("att3", "value3"),
                new ParameterAttributeData("att4", "value4")
              ))
            )
          )
        )
      );
    return data;
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
