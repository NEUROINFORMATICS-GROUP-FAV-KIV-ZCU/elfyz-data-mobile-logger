package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateVersionAndModel;


public class HeartRateVersionAndModelTable extends ATable<HeartRateVersionAndModelTable.HRVersionAndModelObserver>{
  private static final String TAG = HeartRateVersionAndModelTable.class.getSimpleName();

  private static final String TABLE_NAME = "hr_version_and_model";

  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_HW_VERSION = "hw_version";
  public static final String COLUMN_SW_VERSION = "sw_version";
  public static final String COLUMN_MODEL_NUMBER = "model_number";
  
  
  public HeartRateVersionAndModelTable(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }

  
  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_HW_VERSION + " INTEGER NOT NULL,"
        + COLUMN_SW_VERSION + " INTEGER NOT NULL,"
        + COLUMN_MODEL_NUMBER + " INTEGER NOT NULL,"
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

  
  public long addVersionAndModel(long userID, HeartRateVersionAndModel data) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(5);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, data.getEstTimestamp());
      values.put(COLUMN_HW_VERSION, data.getHardwareVersion());
      values.put(COLUMN_SW_VERSION, data.getSoftwareVersion());
      values.put(COLUMN_MODEL_NUMBER, data.getModelNumber());
    
    try{
      long id = db.insertOrThrow(TABLE_NAME, null, values);
      
      for (HRVersionAndModelObserver o : observers) {
        o.onHRVersionAndModelDataAdded(id);
      }
      
      return id;
    }
    catch(SQLException e){
      throw new DatabaseException(e);
    }
  }

  
  
  public interface HRVersionAndModelObserver {
    void onHRVersionAndModelDataAdded(long id);
  }
}
