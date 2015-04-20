package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.ARecordTable.IRecordDataObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.heart_rate.HeartRatePage4;


public class HeartRatePage4Table extends ARecordTable<HeartRatePage4Table.HRPage4Observer> {
  private static final String TAG = HeartRatePage4.class.getSimpleName();

  private static final String TABLE_NAME = "hr_page_4";

  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_MANUFACTURER_SPECIFIC = "man_specific";
  public static final String COLUMN_PREVIOUS_HB_TIME = "prev_hb_time";
  
  private static final String[] COLUMNS_MEASUREMENT_ALL = new String[]{COLUMN_ID, COLUMN_TIME, COLUMN_MANUFACTURER_SPECIFIC, COLUMN_PREVIOUS_HB_TIME, COLUMN_UPLOADED};

  private static final String ORDER_MEASUREMENTS_DESC = COLUMN_TIME + " DESC";
  private static final String ORDER_MEASUREMENTS_ASC = COLUMN_TIME + " ASC";

  
  public HeartRatePage4Table(SQLiteOpenHelper openHelper, int tableID) {
    super(openHelper, tableID);
  }

  
  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_MANUFACTURER_SPECIFIC + " INTEGER NOT NULL,"
        + COLUMN_PREVIOUS_HB_TIME + " INTEGER NOT NULL,"
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

  
  public long addAdditionalData(long userID, HeartRatePage4 data) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(5);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, data.getEstTimestamp());
      values.put(COLUMN_MANUFACTURER_SPECIFIC, data.getManufacturerSpecificByte());
      values.put(COLUMN_PREVIOUS_HB_TIME, data.getPreviousHeartBeatEventTime().longValue());
      values.put(COLUMN_UPLOADED, data.isUploaded() ? VALUE_TRUE : VALUE_FALSE);
    
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
      
      for (HRPage4Observer observer : observers) {
        observer.onHRPage4DataUpdated(ids);
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
  
  
  
  public interface HRPage4Observer extends IRecordDataObserver {
    void onHRPage4DataAdded(long id);
    void onHRPage4DataUpdated(long[] ids);
  }
}
