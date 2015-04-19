package cz.zcu.kiv.mobile.logger.data.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.ARecordTable.IRecordDataObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DuplicateEntryException;
import cz.zcu.kiv.mobile.logger.data.types.glucose.GlucoseMeasurement;


public class GlucoseMeasurementTable extends ARecordTable<GlucoseMeasurementTable.GDataObserver> {
  private static final String TAG = GlucoseMeasurementTable.class.getSimpleName();

  private static final String TABLE_NAME = "glucose_measurement";

  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_GLUCOSE = "glucose";
  public static final String COLUMN_TEMPERATURE = "temperature";
  public static final String COLUMN_CODE = "code";
  public static final String COLUMN_TYPE = "type";
  
  private static final String[] COLUMNS_MEASUREMENT_ALL = new String[]{COLUMN_ID, COLUMN_TIME, COLUMN_GLUCOSE, COLUMN_TEMPERATURE, COLUMN_CODE, COLUMN_TYPE, COLUMN_UPLOADED};

  private static final String ORDER_MEASUREMENTS_DESC = COLUMN_TIME + " DESC";
  private static final String ORDER_MEASUREMENTS_ASC = COLUMN_TIME + " ASC";
  private static final String WHERE_IDS_IN_= COLUMN_ID + " IN ";
  
  
  public GlucoseMeasurementTable(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }
  

  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_GLUCOSE + " INTEGER NOT NULL,"
        + COLUMN_TEMPERATURE + " INTEGER NOT NULL,"
        + COLUMN_CODE + " INTEGER NOT NULL,"
        + COLUMN_TYPE + " INTEGER NOT NULL,"
        + COLUMN_UPLOADED + " INTEGER NOT NULL,"
        + "UNIQUE (" + COLUMN_USER_ID + "," + COLUMN_TIME + "," + COLUMN_GLUCOSE + "," + COLUMN_TEMPERATURE + "," + COLUMN_CODE + "," + COLUMN_TYPE + "),"
        + "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + ProfileTable.TABLE_NAME + " (" + COLUMN_ID + ") ON DELETE CASCADE"
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
  
  
  public long addMeasurement(long userID, GlucoseMeasurement measurement) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    try{
      long id =  insertMeasurement(db, userID, measurement);
      
      for (GDataObserver observer : observers) {
        observer.onGlucoseMeasurementAdded(id);
      }
      
      return id;
    }
    catch(Exception e){
      throw handleException(e);
    }
  }

  public List<Long> addMeasurements(long userID, List<GlucoseMeasurement> measurements, boolean ignoreDuplicates) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    List<Long> ids = new ArrayList<Long>(measurements.size());
    
    try {
      db.beginTransaction();
      
      for (GlucoseMeasurement measurement : measurements) {
        try {
          ids.add(insertMeasurement(db, userID, measurement));
        }
        catch (DuplicateEntryException e) {
          if(!ignoreDuplicates) {
            ids.add(-1L);
            throw e;
          }
        }
      }
      
      db.setTransactionSuccessful();
    }
    catch (Exception e) {
      throw handleException(e);
    }
    finally {
      db.endTransaction();
    }

    for (GDataObserver observer : observers) {
      observer.onGlucoseMeasurementAdded(ids);
    }
    
    return ids;
  }

  public Cursor getMeasurements(long profileID) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    try{
      String[] selectionArgs = new String[]{ String.valueOf(profileID) };
      return db.query(TABLE_NAME, COLUMNS_MEASUREMENT_ALL, WHERE_USER_ID, selectionArgs, null, null, ORDER_MEASUREMENTS_DESC);
    }
    catch(Exception e){
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
      
      for (GDataObserver observer : observers) {
        observer.onGlucoseMeasurementsUpdated(ids);
      }
    }
    catch(Exception e) {
      throw new DatabaseException(e);
    }
  }
  
  private long insertMeasurement(SQLiteDatabase db, long userID, GlucoseMeasurement measurement) throws DatabaseException {
    try {
      return db.insertOrThrow(TABLE_NAME, null, makeValues(userID, measurement));
    }
    catch (Exception e) {
      throw handleException(e);
    }
  }
  
  
  private ContentValues makeValues(long userID, GlucoseMeasurement measurement) {
    ContentValues values = new ContentValues(7);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, measurement.getTime().getTime().getTime());
      values.put(COLUMN_GLUCOSE, measurement.getGlucose());
      values.put(COLUMN_TEMPERATURE, measurement.getTemperature());
      values.put(COLUMN_CODE, measurement.getCode());
      values.put(COLUMN_TYPE, measurement.getType());
      values.put(COLUMN_UPLOADED, measurement.isUploaded() ? VALUE_TRUE : VALUE_FALSE);
    return values;
  }
  
  @Override
  protected String getTableName() {
    return TABLE_NAME;
  }
  
  
  
  public interface GDataObserver extends IRecordDataObserver {
    void onGlucoseMeasurementAdded(long id);
    void onGlucoseMeasurementsUpdated(long[] ids);
    void onGlucoseMeasurementAdded(List<Long> ids);
  }
}
