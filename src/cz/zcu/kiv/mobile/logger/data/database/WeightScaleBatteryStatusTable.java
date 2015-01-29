package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dsi.ant.plugins.antplus.pcc.defines.BatteryStatus;

import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.WeightScaleBatteryStatus;


public class WeightScaleBatteryStatusTable extends ATable<WeightScaleBatteryStatusTable.WSBatteryStatusObserver> {
  private static final String TAG = WeightScaleBatteryStatusTable.class.getSimpleName();

  private static final String TABLE_NAME = "ws_battery_status";

  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_BASIC = "basic";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_CUMUL_OP_TIME = "cumul_op_time";
  public static final String COLUMN_BAT_VOLTAGE = "battery_voltage";
  public static final String COLUMN_BAT_STATUS = "battery_status";
  public static final String COLUMN_CUMUL_OP_TIME_RES = "cumul_op_time_res";
  public static final String COLUMN_BAT_COUNT = "battery_count";
  public static final String COLUMN_BAT_ID = "battery_id";
  
  private static final int BAT_STATE_INVALID = -2;
  private static final int BAT_STATE_UNRECOGNIZED = -1;
  private static final int BAT_STATE_CRITICAL = 0;
  private static final int BAT_STATE_LOW = 1;
  private static final int BAT_STATE_GOOD = 2;
  private static final int BAT_STATE_OK = 3;
  private static final int BAT_STATE_NEW = 4;
  
  
  public WeightScaleBatteryStatusTable(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }

  
  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_CUMUL_OP_TIME + " INTEGER NOT NULL,"
        + COLUMN_BAT_VOLTAGE + " REAL NOT NULL,"
        + COLUMN_BAT_STATUS + " INTEGER NOT NULL,"
        + COLUMN_CUMUL_OP_TIME_RES + " INTEGER NOT NULL,"
        + COLUMN_BAT_COUNT + " INTEGER NOT NULL,"
        + COLUMN_BAT_ID + " INTEGER NOT NULL,"
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
  
  
  public long addBatteryStatus(long userID, WeightScaleBatteryStatus measurement) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(5);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, measurement.getEstTimestamp());
      values.put(COLUMN_CUMUL_OP_TIME, measurement.getCumulativeOperatingTime());
      values.put(COLUMN_BAT_VOLTAGE, measurement.getBatteryVoltage().doubleValue());
      values.put(COLUMN_BAT_STATUS, mapBatteryStatus(measurement.getBatteryStatus()));
      values.put(COLUMN_CUMUL_OP_TIME_RES, measurement.getCumulativeOperatingTimeResolution());
      values.put(COLUMN_BAT_COUNT, measurement.getNumberOfBatteries());
      values.put(COLUMN_BAT_ID, measurement.getBatteryIdentifier());
    
    try{
      long id = db.insertOrThrow(TABLE_NAME, null, values);
      
      for (WSBatteryStatusObserver o : observers) {
        o.onWSBatteryStatusDataAdded(id);
      }
      
      return id;
    }
    catch(SQLException e){
      throw new DatabaseException(e);
    }
  }
  
  
  private int mapBatteryStatus(BatteryStatus batteryStatus) {
    switch (batteryStatus) {
      case UNRECOGNIZED:  return BAT_STATE_UNRECOGNIZED;
      case INVALID:       return BAT_STATE_INVALID;
      case CRITICAL:      return BAT_STATE_CRITICAL;
      case LOW:           return BAT_STATE_LOW;
      case GOOD:          return BAT_STATE_GOOD;
      case OK:            return BAT_STATE_OK;
      case NEW:           return BAT_STATE_NEW;
  
      default: throw new RuntimeException("Unexpected enum value: " + batteryStatus);
    }
  }
  
  private BatteryStatus mapBatteryStatus(int batteryStatus) {
    switch (batteryStatus) {
      case BAT_STATE_UNRECOGNIZED:  return BatteryStatus.UNRECOGNIZED;
      case BAT_STATE_INVALID:       return BatteryStatus.INVALID;
      case BAT_STATE_CRITICAL:      return BatteryStatus.CRITICAL;
      case BAT_STATE_LOW:           return BatteryStatus.LOW;
      case BAT_STATE_GOOD:          return BatteryStatus.GOOD;
      case BAT_STATE_OK:            return BatteryStatus.OK;
      case BAT_STATE_NEW:           return BatteryStatus.NEW;
  
      default: throw new RuntimeException("Unexpected enum value: " + batteryStatus);
    }
  }



  public interface WSBatteryStatusObserver {
    void onWSBatteryStatusDataAdded(long id);
  }
}
