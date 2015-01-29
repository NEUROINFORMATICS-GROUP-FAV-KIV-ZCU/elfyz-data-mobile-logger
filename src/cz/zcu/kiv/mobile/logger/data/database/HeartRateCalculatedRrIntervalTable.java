package cz.zcu.kiv.mobile.logger.data.database;

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.RrFlag;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateCalculatedRrInterval;


public class HeartRateCalculatedRrIntervalTable extends ATable<HeartRateCalculatedRrIntervalTable.HRCalculatedRrIntervalObserver>{
  private static final String TAG = HeartRateCalculatedRrIntervalTable.class.getSimpleName();

  private static final String TABLE_NAME = "hr_calc_rr_interval";

  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_CALC_RR_INTERVAL = "calc_rr_interval";
  public static final String COLUMN_RR_FLAG = "rr_flag";

  private static final int DATA_RRFLAG_UNRECOGNIZED = -1;
  private static final int DATA_RRFLAG_HR_ZERO_DETECTED = 0;
  private static final int DATA_RRFLAG_SOURCE_AVERAGED = 1;
  private static final int DATA_RRFLAG_SOURCE_CACHED = 2;
  private static final int DATA_RRFLAG_SOURCE_PAGE4 = 3;
  
  
  public HeartRateCalculatedRrIntervalTable(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }

  
  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_CALC_RR_INTERVAL + " INTEGER NOT NULL,"
        + COLUMN_RR_FLAG + " INTEGER NOT NULL,"
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

  
  public long addCalculatedRrIntervalData(long userID, HeartRateCalculatedRrInterval data) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(5);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, data.getEstTimestamp());
      values.put(COLUMN_CALC_RR_INTERVAL, data.getCalculatedRrInterval().longValue());
      values.put(COLUMN_RR_FLAG, mapRrFlag(data.getRrFlag()));
    
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

  
  private int mapRrFlag(RrFlag rrFlag) {
    switch (rrFlag) {
      case UNRECOGNIZED:              return DATA_RRFLAG_UNRECOGNIZED;
      case HEART_RATE_ZERO_DETECTED:  return DATA_RRFLAG_HR_ZERO_DETECTED;
      case DATA_SOURCE_AVERAGED:      return DATA_RRFLAG_SOURCE_AVERAGED;
      case DATA_SOURCE_CACHED:        return DATA_RRFLAG_SOURCE_CACHED;
      case DATA_SOURCE_PAGE_4:        return DATA_RRFLAG_SOURCE_PAGE4;
  
      default: throw new RuntimeException("Unexpected enum value: " + rrFlag);
    }
  }
  
  private RrFlag mapRrFlag(int rrFlag) {
    switch (rrFlag) {
      case DATA_RRFLAG_UNRECOGNIZED:      return RrFlag.UNRECOGNIZED;
      case DATA_RRFLAG_HR_ZERO_DETECTED:  return RrFlag.HEART_RATE_ZERO_DETECTED;
      case DATA_RRFLAG_SOURCE_AVERAGED:   return RrFlag.DATA_SOURCE_AVERAGED;
      case DATA_RRFLAG_SOURCE_CACHED:     return RrFlag.DATA_SOURCE_CACHED;
      case DATA_RRFLAG_SOURCE_PAGE4:      return RrFlag.DATA_SOURCE_PAGE_4;
  
      default: throw new RuntimeException("Unexpected enum value: " + rrFlag);
    }
  }
  
  

  public interface HRCalculatedRrIntervalObserver {
    void onHRCalculatedRrIntervalDataAdded(long id);
  }
}
