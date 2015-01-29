package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateCumulativeOperatingTime;


public class HeartRateCumulativeOperatingTimeTable extends ATable<HeartRateCumulativeOperatingTimeTable.HRCumulativeOperatingTimeObserver> {
  private static final String TAG = HeartRateCumulativeOperatingTimeTable.class.getSimpleName();

  private static final String TABLE_NAME = "hr_cumul_op_time";

  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_CUMUL_OP_TIME = "cumul_op_time";
  

  public HeartRateCumulativeOperatingTimeTable(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }

  
  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_CUMUL_OP_TIME + " INTEGER NOT NULL,"
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
  
  
  public long addCumulativeOperatingTime(long userID, HeartRateCumulativeOperatingTime measurement) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(5);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, measurement.getEstTimestamp());
      values.put(COLUMN_CUMUL_OP_TIME, measurement.getCumulativeOperatingTime());
    
    try{
      long id = db.insertOrThrow(TABLE_NAME, null, values);
      
      for (HRCumulativeOperatingTimeObserver o : observers) {
        o.onHRCumulativeOperatingTimeDataAdded(id);
      }
      
      return id;
    }
    catch(SQLException e) {
      throw new DatabaseException(e);
    }
  }
    
  
    
  public interface HRCumulativeOperatingTimeObserver {
    void onHRCumulativeOperatingTimeDataAdded(long id);
  }
}
