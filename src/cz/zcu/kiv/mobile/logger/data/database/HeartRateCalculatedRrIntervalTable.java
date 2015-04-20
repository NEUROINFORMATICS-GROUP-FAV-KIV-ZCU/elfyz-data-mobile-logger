package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cz.zcu.kiv.mobile.logger.data.database.ARecordTable.IRecordDataObserver;
import android.util.Log;

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.RrFlag;

import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.heart_rate.HeartRateCalculatedRrInterval;


public class HeartRateCalculatedRrIntervalTable extends ARecordTable<HeartRateCalculatedRrIntervalTable.HRCalculatedRrIntervalObserver>{
  private static final String TAG = HeartRateCalculatedRrIntervalTable.class.getSimpleName();

  private static final String TABLE_NAME = "hr_calc_rr_interval";

  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_CALC_RR_INTERVAL = "calc_rr_interval";
  public static final String COLUMN_RR_FLAG = "rr_flag";

  private static final int DATA_RRFLAG_UNRECOGNIZED = -1;
  private static final int DATA_RRFLAG_HR_ZERO_DETECTED = 0;
  private static final int DATA_RRFLAG_SOURCE_AVERAGED = 1;
  private static final int DATA_RRFLAG_SOURCE_CACHED = 2;
  private static final int DATA_RRFLAG_SOURCE_PAGE4 = 3;
  
  private static final String[] COLUMNS_MEASUREMENT_ALL = new String[]{COLUMN_ID, COLUMN_TIME, COLUMN_CALC_RR_INTERVAL, COLUMN_RR_FLAG, COLUMN_UPLOADED};

  private static final String ORDER_MEASUREMENTS_DESC = COLUMN_TIME + " DESC";
  private static final String ORDER_MEASUREMENTS_ASC = COLUMN_TIME + " ASC";
  
  
  public HeartRateCalculatedRrIntervalTable(SQLiteOpenHelper openHelper, int tableID) {
    super(openHelper, tableID);
  }

  
  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_CALC_RR_INTERVAL + " INTEGER NOT NULL,"
        + COLUMN_RR_FLAG + " INTEGER NOT NULL,"
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

  
  public long addCalculatedRrIntervalData(long userID, HeartRateCalculatedRrInterval data) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(5);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, data.getEstTimestamp());
      values.put(COLUMN_CALC_RR_INTERVAL, data.getCalculatedRrInterval().longValue());
      values.put(COLUMN_RR_FLAG, mapRrFlag(data.getRrFlag()));
      values.put(COLUMN_UPLOADED, data.isUploaded() ? VALUE_TRUE : VALUE_FALSE);
    
    try{
      long id = db.insertOrThrow(TABLE_NAME, null, values);
      
      for (HRCalculatedRrIntervalObserver o : observers) {
        o.onHRCalculatedRrIntervalDataAdded(id);
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
      
      for (HRCalculatedRrIntervalObserver observer : observers) {
        observer.onHRCalculatedRrIntervalDataUpdated(ids);
      }
    }
    catch(Exception e) {
      throw new DatabaseException(e);
    }
  }

  
  public static int mapRrFlag(RrFlag rrFlag) {
    switch (rrFlag) {
      case UNRECOGNIZED:              return DATA_RRFLAG_UNRECOGNIZED;
      case HEART_RATE_ZERO_DETECTED:  return DATA_RRFLAG_HR_ZERO_DETECTED;
      case DATA_SOURCE_AVERAGED:      return DATA_RRFLAG_SOURCE_AVERAGED;
      case DATA_SOURCE_CACHED:        return DATA_RRFLAG_SOURCE_CACHED;
      case DATA_SOURCE_PAGE_4:        return DATA_RRFLAG_SOURCE_PAGE4;
  
      default: throw new RuntimeException("Unexpected enum value: " + rrFlag);
    }
  }
  
  public static RrFlag mapRrFlag(int rrFlag) {
    switch (rrFlag) {
      case DATA_RRFLAG_UNRECOGNIZED:      return RrFlag.UNRECOGNIZED;
      case DATA_RRFLAG_HR_ZERO_DETECTED:  return RrFlag.HEART_RATE_ZERO_DETECTED;
      case DATA_RRFLAG_SOURCE_AVERAGED:   return RrFlag.DATA_SOURCE_AVERAGED;
      case DATA_RRFLAG_SOURCE_CACHED:     return RrFlag.DATA_SOURCE_CACHED;
      case DATA_RRFLAG_SOURCE_PAGE4:      return RrFlag.DATA_SOURCE_PAGE_4;
  
      default: throw new RuntimeException("Unexpected enum value: " + rrFlag);
    }
  }
  
  @Override
  protected String getTableName() {
    return TABLE_NAME;
  }
  
  

  public interface HRCalculatedRrIntervalObserver extends IRecordDataObserver {
    void onHRCalculatedRrIntervalDataAdded(long id);
    void onHRCalculatedRrIntervalDataUpdated(long[] ids);
  }
}
