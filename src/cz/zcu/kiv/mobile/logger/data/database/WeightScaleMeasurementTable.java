package cz.zcu.kiv.mobile.logger.data.database;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.weight_scale.WeightScaleAdvancedMeasurement;
import cz.zcu.kiv.mobile.logger.data.types.weight_scale.WeightScaleBasicMeasurement;


public class WeightScaleMeasurementTable extends ARecordTable<WeightScaleMeasurementTable.WSDataObserver> {
  private static final String TAG = WeightScaleMeasurementTable.class.getSimpleName();

  private static final String TABLE_NAME = "ws_measurement";

  public static final String COLUMN_BASIC = "basic";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_WEIGHT = "weight";
  public static final String COLUMN_HYDRATION_PERCENTAGE = "hydration_percentage";
  public static final String COLUMN_FAT_PERCENTAGE = "fat_percentage";
  public static final String COLUMN_MUSCLE_MASS = "muscle_mass";
  public static final String COLUMN_BONE_MASS = "bone_mass";
  public static final String COLUMN_ACTIVE_METABOLIC_RATE = "active_metabolic_rate";
  public static final String COLUMN_BASAL_METABOLIC_RATE = "basal_metabolic_rate";
  
  private static final String[] COLUMNS_MEASUREMENT_ALL = new String[]{COLUMN_ID, COLUMN_TIME, COLUMN_BASIC, COLUMN_WEIGHT, COLUMN_HYDRATION_PERCENTAGE, COLUMN_FAT_PERCENTAGE, COLUMN_MUSCLE_MASS, COLUMN_BONE_MASS, COLUMN_ACTIVE_METABOLIC_RATE, COLUMN_BASAL_METABOLIC_RATE, COLUMN_UPLOADED};

  private static final String ORDER_MEASUREMENTS_DESC = COLUMN_TIME + " DESC";
  private static final String ORDER_MEASUREMENTS_ASC = COLUMN_TIME + " ASC";
  
  
  public WeightScaleMeasurementTable(SQLiteOpenHelper openHelper, int tableID) {
    super(openHelper, tableID);
  }

  
  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_BASIC + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_WEIGHT + " REAL NOT NULL,"
        + COLUMN_HYDRATION_PERCENTAGE + " REAL NULL,"
        + COLUMN_FAT_PERCENTAGE + " REAL NULL,"
        + COLUMN_MUSCLE_MASS + " REAL NULL,"
        + COLUMN_BONE_MASS + " REAL NULL,"
        + COLUMN_ACTIVE_METABOLIC_RATE + " REAL NULL,"
        + COLUMN_BASAL_METABOLIC_RATE + " REAL NULL,"
        + COLUMN_UPLOADED + " INTEGER NOT NULL,"
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
  
  
  public long addMeasurement(long userID,
      boolean basic, long estTimestamp, double weight, Double hydrationPercentage,
      Double fatPercentage, Double muscleMass, Double boneMass,
      Double activeMetabolicRate, Double basalMetabolicRate, boolean uploaded) throws DatabaseException {
    
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(11);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_BASIC, basic ? VALUE_TRUE : VALUE_FALSE);
      values.put(COLUMN_TIME, estTimestamp);
      values.put(COLUMN_WEIGHT, weight);
      values.put(COLUMN_HYDRATION_PERCENTAGE, hydrationPercentage);
      values.put(COLUMN_FAT_PERCENTAGE, fatPercentage);
      values.put(COLUMN_MUSCLE_MASS, muscleMass);
      values.put(COLUMN_BONE_MASS, boneMass);
      values.put(COLUMN_ACTIVE_METABOLIC_RATE, activeMetabolicRate);
      values.put(COLUMN_BASAL_METABOLIC_RATE, basalMetabolicRate);
      values.put(COLUMN_UPLOADED, uploaded ? VALUE_TRUE : VALUE_FALSE);
    
    try{
      long id = db.insertOrThrow(TABLE_NAME, null, values);
      
      for (WSDataObserver o : observers) {
        o.onWSMeasurementAdded(id);
      }
      
      return id;
    }
    catch(Exception e){
      throw new DatabaseException(e);
    }
  }
  
  public long addBasicMeasurement(long userID, WeightScaleBasicMeasurement data) throws DatabaseException {
    return addMeasurement(userID, true, data.getEstTimestamp(), data.getBodyWeight().doubleValue(), null, null, null, null, null, null, data.isUploaded());
  }
  
  public long addAdvancedMeasurement(long userID, WeightScaleAdvancedMeasurement data) throws DatabaseException {
    return addMeasurement(userID, false, data.getEstTimestamp(), data.getBodyWeight().doubleValue(), 
        data.getHydrationPercentage().doubleValue(), data.getBodyFatPercentage().doubleValue(),
        data.getMuscleMass().doubleValue(), data.getBoneMass().doubleValue(),
        data.getActiveMetabolicRate().doubleValue(), data.getBasalMetabolicRate().doubleValue(), data.isUploaded());
  }

  public Cursor getMeasurements(long profileID) throws DatabaseException {
    return getMeasurements(profileID, false);
  }

  public Cursor getMeasurements(long profileID, boolean ascending) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    try {
      String[] selectionArgs = new String[]{ String.valueOf(profileID) };
      return db.query(TABLE_NAME, COLUMNS_MEASUREMENT_ALL, WHERE_USER_ID, selectionArgs, null, null, ascending ? ORDER_MEASUREMENTS_ASC : ORDER_MEASUREMENTS_DESC);
    }
    catch(Exception e) {
      throw new DatabaseException(e);
    }
  }

  public Cursor getMeasurements(long profileID, boolean ascending, Date dateFrom, Date dateTo) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    try {
      String[] selectionArgs = new String[]{
          String.valueOf(profileID),
          String.valueOf(dateFrom.getTime()),
          String.valueOf(dateTo.getTime())};
      return db.query(TABLE_NAME, COLUMNS_MEASUREMENT_ALL, WHERE_USER_ID_AND_DATES, selectionArgs, null, null, ascending ? ORDER_MEASUREMENTS_ASC : ORDER_MEASUREMENTS_DESC);
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
      
      for (WSDataObserver observer : observers) {
        observer.onWSMeasurementsUpdated(ids);
      }
    }
    catch(Exception e) {
      throw new DatabaseException(e);
    }
  }
  
  @Override
  protected String getTableName() {
    return TABLE_NAME;
  }
  
  

  public interface WSDataObserver extends ARecordTable.IRecordDataObserver {
    void onWSMeasurementAdded(long id);
    void onWSMeasurementsUpdated(long[] ids);
  }
}
