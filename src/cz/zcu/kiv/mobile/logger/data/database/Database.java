package cz.zcu.kiv.mobile.logger.data.database;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.tables.BloodPressureMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.tables.HeartRateMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.tables.ProfileTable;
import cz.zcu.kiv.mobile.logger.data.database.tables.WeightScaleMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.types.Gender;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.devices.blood_pressure.BloodPressureMeasurement;


public class Database {
  private static final String TAG = Database.class.getSimpleName();

  private static final String DATABASE_NAME = "diplomka_database";
  private static final int DATABASE_VERSION = 3;

  private SQLiteOpenHelper openHelper;
  
  private ProfileTable profileTable;
  private BloodPressureMeasurementTable bpmTable;
  private HeartRateMeasurementTable hrTable;
  private WeightScaleMeasurementTable wsTable;



  public Database(Context context){
    openHelper = new DatabaseHelper(context);
    
    profileTable = new ProfileTable();
    bpmTable = new BloodPressureMeasurementTable();
    hrTable = new HeartRateMeasurementTable();
    wsTable = new WeightScaleMeasurementTable();
  }


  public long createProfile(String profileName, Calendar birthDate, Gender gender, int height, int activityLevel, boolean lifetimeAthlete) throws DatabaseException {
    Log.e(TAG, "createProfile");
    return profileTable.createProfile(openHelper.getWritableDatabase(), profileName, birthDate, gender, height, activityLevel, lifetimeAthlete);
  }

  public void deleteProfile(long profileID) throws DatabaseException {
    Log.e(TAG, "deleteProfile");
    profileTable.deleteProfile(openHelper.getWritableDatabase(), profileID);
  }

  public Cursor getProfileNames() throws DatabaseException {
    Log.e(TAG, "getProfileNames");
    return profileTable.getProfileNames(openHelper.getWritableDatabase());
  }
  
  public Profile getProfile(long profileId) throws DatabaseException {
    Log.e(TAG, "getProfile");
    return profileTable.getProfile(openHelper.getReadableDatabase(), profileId);
  }

  public long addBloodPressureMeasurement(long userID, BloodPressureMeasurement measurement) throws DatabaseException {
    Log.e(TAG, "addBloodPressureMeasurement");
    return bpmTable.addMeasurement(openHelper.getWritableDatabase(), userID, measurement);
  }

  public long addHeartRateMeasurement(long userID, int computedHeartRate, long heartBeatCount, long heartBeatEventTime) throws DatabaseException {
    Log.e(TAG, "addHeartRateMeasurement");
    return hrTable.addMeasurement(openHelper.getWritableDatabase(), userID, computedHeartRate, heartBeatCount, heartBeatEventTime);
  }

  public long addWeightScaleBasicMeasurement(long userID, double weight) throws DatabaseException {
    Log.e(TAG, "addWeightScaleBasicMeasurement");
    return wsTable.addMeasurement(openHelper.getWritableDatabase(), userID, true, new Date(), weight, null, null, null, null, null, null);
  }

  public long addWeightScaleAdvancedMeasurement(long userID, double weight, double hydrationPercentage, double fatPercentage, double muscleMass, double boneMass, double activeMetabolicRate, double basalMetabolicRate) throws DatabaseException {
    Log.e(TAG, "addWeightScaleAdvancedMeasurement");
    return wsTable.addMeasurement(openHelper.getWritableDatabase(), userID, false, new Date(), weight, hydrationPercentage, fatPercentage, muscleMass, boneMass, activeMetabolicRate, basalMetabolicRate);
  }


  public void close(){
    openHelper.close();
  }

  

  private class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      profileTable.onCreate(db);
      bpmTable.onCreate(db);
      hrTable.onCreate(db);
      wsTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
      profileTable.onUpgrade(db, oldVersion, currentVersion);
      bpmTable.onUpgrade(db, oldVersion, currentVersion);
      hrTable.onUpgrade(db, oldVersion, currentVersion);
      wsTable.onUpgrade(db, oldVersion, currentVersion);
    }
  }
}
