package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.heart_rate.HeartRateCumulativeOperatingTime;


public class HeartRateCumulativeOperatingTimeTable extends ARecordTable<HeartRateCumulativeOperatingTimeTable.HRCumulativeOperatingTimeObserver> {
  private static final String TAG = HeartRateCumulativeOperatingTimeTable.class.getSimpleName();

  private static final String TABLE_NAME = "hr_cumul_op_time";

  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_CUMUL_OP_TIME = "cumul_op_time";
  
  private static final String[] COLUMNS_MEASUREMENT_ALL = new String[]{COLUMN_ID, COLUMN_TIME, COLUMN_CUMUL_OP_TIME, COLUMN_UPLOADED};

  private static final String ORDER_MEASUREMENTS_DESC = COLUMN_TIME + " DESC";
  private static final String ORDER_MEASUREMENTS_ASC = COLUMN_TIME + " ASC";
  

  public HeartRateCumulativeOperatingTimeTable(SQLiteOpenHelper openHelper, int tableID) {
    super(openHelper, tableID);
  }

  
  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_CUMUL_OP_TIME + " INTEGER NOT NULL,"
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
  
  
  public long addCumulativeOperatingTime(long userID, HeartRateCumulativeOperatingTime measurement) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(4);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, measurement.getEstTimestamp());
      values.put(COLUMN_CUMUL_OP_TIME, measurement.getCumulativeOperatingTime());
      values.put(COLUMN_UPLOADED, measurement.isUploaded() ? VALUE_TRUE : VALUE_FALSE);
    
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
      
      for (HRCumulativeOperatingTimeObserver observer : observers) {
        observer.onHRCumulativeOperatingTimeDataUpdated(ids);
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
    
  
    
  public interface HRCumulativeOperatingTimeObserver extends ARecordTable.IRecordDataObserver {
    void onHRCumulativeOperatingTimeDataAdded(long id);
    void onHRCumulativeOperatingTimeDataUpdated(long[] ids);
  }
}
