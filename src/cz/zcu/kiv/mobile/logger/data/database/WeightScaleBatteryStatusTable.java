package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.Cursor;
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
  
  private static final String[] COLUMNS_MEASUREMENT_ALL = new String[]{COLUMN_ID, COLUMN_TIME, COLUMN_CUMUL_OP_TIME,
    COLUMN_BAT_VOLTAGE, COLUMN_BAT_STATUS, COLUMN_CUMUL_OP_TIME_RES, COLUMN_BAT_COUNT, COLUMN_BAT_ID, COLUMN_UPLOADED};

  private static final String ORDER_MEASUREMENTS_DESC = COLUMN_TIME + " DESC";
  private static final String ORDER_MEASUREMENTS_ASC = COLUMN_TIME + " ASC";
  private static final String WHERE_USER_ID = COLUMN_USER_ID + " = ? ";
  private static final String WHERE_IDS_IN_ = COLUMN_ID + " IN ";
  
  
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
        + COLUMN_UPLOADED + " INTEGER NOT NULL,"
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
    
    ContentValues values = new ContentValues(9);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, measurement.getEstTimestamp());
      values.put(COLUMN_CUMUL_OP_TIME, measurement.getCumulativeOperatingTime());
      values.put(COLUMN_BAT_VOLTAGE, measurement.getBatteryVoltage().doubleValue());
      values.put(COLUMN_BAT_STATUS, mapBatteryStatus(measurement.getBatteryStatus()));
      values.put(COLUMN_CUMUL_OP_TIME_RES, measurement.getCumulativeOperatingTimeResolution());
      values.put(COLUMN_BAT_COUNT, measurement.getNumberOfBatteries());
      values.put(COLUMN_BAT_ID, measurement.getBatteryIdentifier());
      values.put(COLUMN_UPLOADED, measurement.isUploaded() ? VALUE_TRUE : VALUE_FALSE);
    
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
      
      for (WSBatteryStatusObserver observer : observers) {
        observer.onWSBatteryStatusDataUpdated(ids);
      }
    }
    catch(Exception e) {
      throw new DatabaseException(e);
    }
  }
  
  
  public static int mapBatteryStatus(BatteryStatus batteryStatus) {
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
  
  public static BatteryStatus mapBatteryStatus(int batteryStatus) {
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
    void onWSBatteryStatusDataUpdated(long[] ids);
  }
}
