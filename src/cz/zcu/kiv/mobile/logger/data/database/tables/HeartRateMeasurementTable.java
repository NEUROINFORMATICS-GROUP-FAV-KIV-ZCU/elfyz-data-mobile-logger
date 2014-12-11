package cz.zcu.kiv.mobile.logger.data.database.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;


public class HeartRateMeasurementTable extends ATable {
  private static final String TAG = HeartRateMeasurementTable.class.getSimpleName();

  private static final String TABLE_NAME = "heart_rate_measurement";

  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_RATE = "rate";
  public static final String COLUMN_COUNT = "count";
  
//  private static final String[] COLUMNS_MEASUREMENT_ALL = new String[]{COLUMN_ID, COLUMN_USER_ID, COLUMN_TIME, COLUMN_RATE, COLUMN_COUNT};
//    
//  private static final String ORDER_MEASUREMENTS_ALL_DESC = COLUMN_TIME + " DESC";

  
  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_RATE + " INTEGER NOT NULL,"
        + COLUMN_COUNT + " INTEGER NOT NULL,"
        + "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + ProfileTable.TABLE_NAME + " (" + COLUMN_ID + ")"
        + ");");
  }
  
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
    int upgradeVersion = oldVersion;

    if(upgradeVersion != currentVersion){
      Log.d(TAG, "Wasn't able to upgrade the database. Wiping and rebuilding...");

      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

      onCreate(db);
    }
  }
  
  
  public long addMeasurement(SQLiteDatabase db, long userID, int computedHeartRate, long heartBeatCount, long heartBeatEventTime) throws DatabaseException {
    ContentValues values = new ContentValues(1);
    values.put(COLUMN_USER_ID, userID);
    values.put(COLUMN_TIME, heartBeatEventTime);
    values.put(COLUMN_RATE, computedHeartRate);
    values.put(COLUMN_COUNT, heartBeatCount);
    
    try{
      return db.insertOrThrow(TABLE_NAME, null, values);
    }
    catch(Exception e){
      throw new DatabaseException(e);
    }
  }
}
