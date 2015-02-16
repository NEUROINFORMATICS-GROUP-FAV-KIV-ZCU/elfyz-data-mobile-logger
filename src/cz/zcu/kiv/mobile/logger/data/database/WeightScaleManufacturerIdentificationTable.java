package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.WeightScaleManufacturerIdentification;


public class WeightScaleManufacturerIdentificationTable extends ATable<WeightScaleManufacturerIdentificationTable.WSManufacturerIdentificationObserver> {
  private static final String TAG = WeightScaleManufacturerIdentificationTable.class.getSimpleName();

  private static final String TABLE_NAME = "ws_manuf_id";

  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_HW_REV = "hw_rev";
  public static final String COLUMN_MANUF_ID = "manuf_id";
  public static final String COLUMN_MODEL_NR = "model_nr";
  
  
  public WeightScaleManufacturerIdentificationTable(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }

  
  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_HW_REV + " INTEGER NOT NULL,"
        + COLUMN_MANUF_ID + " INTEGER NOT NULL,"
        + COLUMN_MODEL_NR + " INTEGER NOT NULL,"
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
  
  
  public long addManufacturerIdentification(long userID, WeightScaleManufacturerIdentification measurement) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(5);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, measurement.getEstTimestamp());
      values.put(COLUMN_HW_REV, measurement.getHardwareRevision());
      values.put(COLUMN_MANUF_ID, measurement.getManufacturerID());
      values.put(COLUMN_MODEL_NR, measurement.getModelNumber());
    
    try{
      long id = db.insertOrThrow(TABLE_NAME, null, values);
      
      for (WSManufacturerIdentificationObserver o : observers) {
        o.onWSManufacturerIdentification(id);
      }
      
      return id;
    }
    catch(SQLException e){
      throw new DatabaseException(e);
    }
  }
  
  
  
  public interface WSManufacturerIdentificationObserver {
    void onWSManufacturerIdentification(long id);
  }
}