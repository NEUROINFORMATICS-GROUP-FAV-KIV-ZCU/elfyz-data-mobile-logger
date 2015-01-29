package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRatePage4;


public class HeartRatePage4Table extends ATable<HeartRatePage4Table.HRPage4Observer> {
  private static final String TAG = HeartRatePage4.class.getSimpleName();

  private static final String TABLE_NAME = "hr_page_4";

  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_MANUFACTURER_SPECIFIC = "man_specific";
  public static final String COLUMN_PREVIOUS_HB_TIME = "prev_hb_time";

  
  public HeartRatePage4Table(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }

  
  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_MANUFACTURER_SPECIFIC + " INTEGER NOT NULL,"
        + COLUMN_PREVIOUS_HB_TIME + " INTEGER NOT NULL,"
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

  
  public long addAdditionalData(long userID, HeartRatePage4 data) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(5);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, data.getEstTimestamp());
      values.put(COLUMN_MANUFACTURER_SPECIFIC, data.getManufacturerSpecificByte());
      values.put(COLUMN_PREVIOUS_HB_TIME, data.getPreviousHeartBeatEventTime().longValue());
    
    try{
      long id = db.insertOrThrow(TABLE_NAME, null, values);
      
      for (HRPage4Observer o : observers) {
        o.onHRPage4DataAdded(id);
      }
      
      return id;
    }
    catch(SQLException e){
      throw new DatabaseException(e);
    }
  }
  
  
  
  public interface HRPage4Observer {
    void onHRPage4DataAdded(long id);
  }
}
