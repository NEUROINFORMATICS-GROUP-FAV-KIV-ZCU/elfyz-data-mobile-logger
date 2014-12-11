package cz.zcu.kiv.mobile.logger.data.database.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.blood_pressure.BloodPressureMeasurement;


public class BloodPressureMeasurementTable extends ATable {
  private static final String TAG = BloodPressureMeasurementTable.class.getSimpleName();

  private static final String TABLE_NAME = "blood_pressure_measurement";

  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_SYSTOLIC = "systolic";
  public static final String COLUMN_DIASTOLIC = "diastolic";
  public static final String COLUMN_MEAN_PRESSURE = "mean";
  public static final String COLUMN_HEART_RATE = "heart_rate";
  
//  private static final String[] COLUMNS_MEASUREMENT_ALL = new String[]{COLUMN_ID, COLUMN_TIME, COLUMN_SYSTOLIC, COLUMN_DIASTOLIC, COLUMN_MEAN_PRESSURE, COLUMN_HEART_RATE};
//    
//  private static final String ORDER_MEASUREMENTS_ALL_DESC = COLUMN_TIME + " DESC";

  
  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_SYSTOLIC + " INTEGER NOT NULL,"
        + COLUMN_DIASTOLIC + " INTEGER NOT NULL,"
        + COLUMN_MEAN_PRESSURE + " INTEGER NOT NULL,"
        + COLUMN_HEART_RATE + " INTEGER NOT NULL,"
        + "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + ProfileTable.TABLE_NAME + " (" + COLUMN_ID + ")"
        + ");");
  }
  
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
    int upgradeVersion = oldVersion;

    if(upgradeVersion != currentVersion){
      Log.d(TAG, "Wasn't able to upgrade the database. Wiping and rebuilding...");

      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

      onCreate(db);
    }
  }
  
  
  public long addMeasurement(SQLiteDatabase db, long userID, BloodPressureMeasurement measurement) throws DatabaseException {
    ContentValues values = new ContentValues(1);
    values.put(COLUMN_USER_ID, userID);
    values.put(COLUMN_TIME, measurement.getTime().getTime().getTime());
    values.put(COLUMN_SYSTOLIC, measurement.getSystolicPressure());
    values.put(COLUMN_DIASTOLIC, measurement.getDiastolicPressure());
    values.put(COLUMN_MEAN_PRESSURE, measurement.getMeanPressure());
    values.put(COLUMN_HEART_RATE, measurement.getHeartRate());
    
    try{
      return db.insertOrThrow(TABLE_NAME, null, values);
    }
    catch(Exception e){
      throw new DatabaseException(e);
    }
  }
}
