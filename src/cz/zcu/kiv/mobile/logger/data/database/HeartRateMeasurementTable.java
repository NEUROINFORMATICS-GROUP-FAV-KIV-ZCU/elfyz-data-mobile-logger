package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.DataState;

import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateMeasurement;


public class HeartRateMeasurementTable extends ATable<HeartRateMeasurementTable.HRDataObserver> {
  private static final String TAG = HeartRateMeasurementTable.class.getSimpleName();

  private static final String TABLE_NAME = "hr_measurement";

  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_HEART_RATE = "heart_rate";
  public static final String COLUMN_BEAT_COUNT = "beat_count";
  public static final String COLUMN_BEAT_TIME = "beat_time";
  public static final String COLUMN_DATA_STATE = "data_state";
  
  private static final int DATA_STATE_UNRECOGNIZED = -1;  //TODO own enum with same values? and support for DB mapping
  private static final int DATA_STATE_ZERO_DETECTED = 0;
  private static final int DATA_STATE_INITIAL_VALUE = 1;
  private static final int DATA_STATE_LIVE_DATA = 2;
  
  private static final String[] COLUMNS_MEASUREMENT_ALL = new String[]{COLUMN_ID, COLUMN_TIME, COLUMN_HEART_RATE, COLUMN_BEAT_COUNT, COLUMN_BEAT_TIME, COLUMN_DATA_STATE, COLUMN_UPLOADED};

  private static final String ORDER_MEASUREMENTS_DESC = COLUMN_TIME + " DESC";
  private static final String ORDER_MEASUREMENTS_ASC = COLUMN_TIME + " ASC";
  private static final String WHERE_USER_ID = COLUMN_USER_ID + " = ? ";
  private static final String WHERE_IDS_IN_ = COLUMN_ID + " IN ";

  
  public HeartRateMeasurementTable(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }
  

  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_HEART_RATE + " INTEGER NOT NULL,"
        + COLUMN_BEAT_COUNT + " INTEGER NOT NULL,"
        + COLUMN_BEAT_TIME + " INTEGER NOT NULL,"
        + COLUMN_DATA_STATE + " INTEGER NOT NULL,"
        + COLUMN_UPLOADED + " INTEGER NOT NULL,"
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
  
  
  public long addMeasurement(long userID, HeartRateMeasurement measurement) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(7);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, measurement.getEstTimestamp());
      values.put(COLUMN_HEART_RATE, measurement.getComputedHeartRate());
      values.put(COLUMN_BEAT_COUNT, measurement.getHeartBeatCount());
      values.put(COLUMN_BEAT_TIME, measurement.getHeartBeatEventTime().longValue());
      values.put(COLUMN_DATA_STATE, mapDataState(measurement.getDataState()));
      values.put(COLUMN_UPLOADED, measurement.isUploaded() ? VALUE_TRUE : VALUE_FALSE);
    
    try{
      long id = db.insertOrThrow(TABLE_NAME, null, values);
      
      for (HRDataObserver o : observers) {
        o.onHRMeasurementAdded(id);
      }
      
      return id;
    }
    catch(SQLException e){
      throw new DatabaseException(e);
    }
  }

  public Cursor getMeasurements(long profileID) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    try {
      String[] selectionArgs = new String[]{ String.valueOf(profileID) };
      return db.query(TABLE_NAME, COLUMNS_MEASUREMENT_ALL, WHERE_USER_ID, selectionArgs, null, null, ORDER_MEASUREMENTS_DESC);
    }
    catch(Exception e) {
      throw new DatabaseException(e);
    }
  }

  public Cursor getMeasurements(long[] ids) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    try {
      return db.query(TABLE_NAME, COLUMNS_MEASUREMENT_ALL, WHERE_IDS_IN_ + assemblePlaceholders(ids.length), toStringArray(ids), null, null, ORDER_MEASUREMENTS_ASC);
    }
    catch(Exception e) {
      throw new DatabaseException(e);
    }
  }

  public void setUploaded(long[] ids) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    try {
      ContentValues values = new ContentValues(1);
      values.put(COLUMN_UPLOADED, VALUE_TRUE);
      
      db.update(TABLE_NAME, values, WHERE_IDS_IN_ + assemblePlaceholders(ids.length), toStringArray(ids));
      
      for (HRDataObserver observer : observers) {
        observer.onHRMeasurementsUpdated(ids);
      }
    }
    catch(Exception e) {
      throw new DatabaseException(e);
    }
  }
  
  
  public static int mapDataState(DataState dataState) {
    switch (dataState) {
      case UNRECOGNIZED:  return DATA_STATE_UNRECOGNIZED;
      case ZERO_DETECTED: return DATA_STATE_ZERO_DETECTED;
      case INITIAL_VALUE: return DATA_STATE_INITIAL_VALUE;
      case LIVE_DATA:     return DATA_STATE_LIVE_DATA;
      
      default: throw new RuntimeException("Unexpected enum value: " + dataState);
    }
  }
  
  public static DataState mapDataState(int dataState) {
    switch (dataState) {
      case DATA_STATE_UNRECOGNIZED:  return DataState.UNRECOGNIZED;
      case DATA_STATE_ZERO_DETECTED: return DataState.ZERO_DETECTED;
      case DATA_STATE_INITIAL_VALUE: return DataState.INITIAL_VALUE;
      case DATA_STATE_LIVE_DATA:     return DataState.LIVE_DATA;
      
      default: throw new RuntimeException("Unexpected enum value: " + dataState);
    }
  }



  public interface HRDataObserver {
    void onHRMeasurementAdded(long id);
    void onHRMeasurementsUpdated(long[] ids);
  }
}
