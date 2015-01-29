package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.WeightScaleAdvancedMeasurement;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.WeightScaleBasicMeasurement;


public class WeightScaleMeasurementTable extends ATable<WeightScaleMeasurementTable.WSDataObserver> {
  private static final String TAG = WeightScaleMeasurementTable.class.getSimpleName();

  private static final String TABLE_NAME = "ws_measurement";

  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_BASIC = "basic";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_WEIGHT = "weight";
  public static final String COLUMN_HYDRATION_PERCENTAGE = "hydration_percentage";
  public static final String COLUMN_FAT_PERCENTAGE = "fat_percentage";
  public static final String COLUMN_MUSCLE_MASS = "muscle_mass";
  public static final String COLUMN_BONE_MASS = "bone_mass";
  public static final String COLUMN_ACTIVE_METABOLIC_RATE = "active_metabolic_rate";
  public static final String COLUMN_BASAL_METABOLIC_RATE = "basal_metabolic_rate";
  
//  private static final String[] COLUMNS_MEASUREMENT_ALL = new String[]{COLUMN_ID, COLUMN_BASIC, COLUMN_TIME, COLUMN_WEIGHT, COLUMN_HYDRATION_PERCENTAGE, COLUMN_FAT_PERCENTAGE, COLUMN_MUSCLE_MASS, COLUMN_MUSCLE_MASS, COLUMN_BONE_MASS, COLUMN_ACTIVE_METABOLIC_RATE, COLUMN_BASAL_METABOLIC_RATE};
//    
//  private static final String ORDER_MEASUREMENTS_ALL_DESC = COLUMN_TIME + " DESC";
  
  
  public WeightScaleMeasurementTable(SQLiteOpenHelper openHelper) {
    super(openHelper);
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
  
  
  public long addMeasurement(long userID,
      boolean basic, long estTimestamp, double weight, Double hydrationPercentage,
      Double fatPercentage, Double muscleMass, Double boneMass,
      Double activeMetabolicRate, Double basalMetabolicRate) throws DatabaseException {
    
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(10);
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
    return addMeasurement(userID, true, data.getEstTimestamp(), data.getBodyWeight().doubleValue(), null, null, null, null, null, null);
  }
  
  public long addAdvancedMeasurement(long userID, WeightScaleAdvancedMeasurement data) throws DatabaseException {
    return addMeasurement(userID, false, data.getEstTimestamp(), data.getBodyWeight().doubleValue(), data.getHydrationPercentage().doubleValue(),
        data.getBodyFatPercentage().doubleValue(), data.getMuscleMass().doubleValue(), data.getBoneMass().doubleValue(),
        data.getActiveMetabolicRate().doubleValue(), data.getBasalMetabolicRate().doubleValue());
  }
  
  

  public interface WSDataObserver {
    void onWSMeasurementAdded(long id);
  }
}
