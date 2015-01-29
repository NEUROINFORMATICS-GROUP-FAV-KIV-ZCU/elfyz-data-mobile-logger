package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateManufacturerAndSerial;


public class HeartRateManufacturerAndSerialTable extends ATable<HeartRateManufacturerAndSerialTable.HRManufacturerAndSerialObserver>{
  private static final String TAG = HeartRateManufacturerAndSerialTable.class.getSimpleName();

  private static final String TABLE_NAME = "hr_manuf_and_serial";

  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_MANUFACTURER_ID = "manufacturer_id";
  public static final String COLUMN_SERIAL_NUMBER = "serial_number";
  
  
  public HeartRateManufacturerAndSerialTable(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }


  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_MANUFACTURER_ID + " INTEGER NOT NULL,"
        + COLUMN_SERIAL_NUMBER + " INTEGER NOT NULL,"
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
  
  
  public long addManufacturerAndSerial(long userID, HeartRateManufacturerAndSerial measurement) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(5);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, measurement.getEstTimestamp());
      values.put(COLUMN_MANUFACTURER_ID, measurement.getManufacturerID());
      values.put(COLUMN_SERIAL_NUMBER, measurement.getSerialNumber());
    
    try{
      long id = db.insertOrThrow(TABLE_NAME, null, values);
      
      for (HRManufacturerAndSerialObserver o : observers) {
        o.onHRManufacturerAndSerialDataAdded(id);
      }
      
      return id;
    }
    catch(SQLException e) {
      throw new DatabaseException(e);
    }
  }
  
  

  public interface HRManufacturerAndSerialObserver {
    void onHRManufacturerAndSerialDataAdded(long id);
  }
}
