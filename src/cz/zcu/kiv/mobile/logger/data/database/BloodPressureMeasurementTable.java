package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.blood_pressure.BloodPressureMeasurement;


public class BloodPressureMeasurementTable extends ATable<BloodPressureMeasurementTable.BPDataObserver> {
  private static final String TAG = BloodPressureMeasurementTable.class.getSimpleName();

  private static final String TABLE_NAME = "blood_pressure_measurement";

  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_SYSTOLIC = "systolic";
  public static final String COLUMN_DIASTOLIC = "diastolic";
  public static final String COLUMN_MEAN_PRESSURE = "mean";
  public static final String COLUMN_HEART_RATE = "heart_rate";
  
  private static final String[] COLUMNS_MEASUREMENT_ALL = new String[]{COLUMN_ID, COLUMN_TIME, COLUMN_SYSTOLIC, COLUMN_DIASTOLIC, COLUMN_MEAN_PRESSURE, COLUMN_HEART_RATE};

  private static final String ORDER_MEASUREMENTS_ALL_DESC = COLUMN_TIME + " DESC";
  private static final String WHERE_USER_ID = COLUMN_USER_ID + " = ? ";
  
  
  public BloodPressureMeasurementTable(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }

  
  @Override
  void onCreate(SQLiteDatabase db) {
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
  void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
    int upgradeVersion = oldVersion;

    if(upgradeVersion != currentVersion){
      Log.d(TAG, "Wasn't able to upgrade the database. Wiping and rebuilding...");

      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

      onCreate(db);
    }
  }
  
  
  public long addMeasurement(long userID, BloodPressureMeasurement measurement) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(1);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, measurement.getTime().getTime().getTime());
      values.put(COLUMN_SYSTOLIC, measurement.getSystolicPressure());
      values.put(COLUMN_DIASTOLIC, measurement.getDiastolicPressure());
      values.put(COLUMN_MEAN_PRESSURE, measurement.getMeanPressure());
      values.put(COLUMN_HEART_RATE, measurement.getHeartRate());
    
    try{
      long id =  db.insertOrThrow(TABLE_NAME, null, values);
      
      for (BPDataObserver observer : observers) {
        observer.onBPMeasurementAdded(id);
      }
      
      return id;
    }
    catch(Exception e){
      throw new DatabaseException(e);
    }
  }

  public Cursor getMeasurements(long profileID) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    try{
      String[] selectionArgs = new String[]{ String.valueOf(profileID) };
      return db.query(TABLE_NAME, COLUMNS_MEASUREMENT_ALL, WHERE_USER_ID, selectionArgs, null, null, ORDER_MEASUREMENTS_ALL_DESC);
    }
    catch(Exception e){
      throw new DatabaseException(e);
    }
  }
  
  
  
  public interface BPDataObserver {
    void onBPMeasurementAdded(long id);
  }
}
