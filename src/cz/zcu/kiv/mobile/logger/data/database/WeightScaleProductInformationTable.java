package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.ARecordTable.IRecordDataObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.weight_scale.WeightScaleProductInformation;


public class WeightScaleProductInformationTable extends ARecordTable<WeightScaleProductInformationTable.WSProductInformationObserver> {
  private static final String TAG = WeightScaleProductInformationTable.class.getSimpleName();

  private static final String TABLE_NAME = "ws_prod_info";

  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_MAIN_SW_REV = "sw_rev_main";
  public static final String COLUMN_SUPP_SW_REV = "sw_rev_supp";
  public static final String COLUMN_SERIAL_NR = "serial_nr";
  
  private static final String[] COLUMNS_MEASUREMENT_ALL = new String[]{COLUMN_ID, COLUMN_TIME, COLUMN_MAIN_SW_REV, COLUMN_SUPP_SW_REV, COLUMN_SERIAL_NR, COLUMN_UPLOADED};

  private static final String ORDER_MEASUREMENTS_DESC = COLUMN_TIME + " DESC";
  private static final String ORDER_MEASUREMENTS_ASC = COLUMN_TIME + " ASC";
  private static final String WHERE_IDS_IN_ = COLUMN_ID + " IN ";
  
  
  public WeightScaleProductInformationTable(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }

  
  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TIME + " INTEGER NOT NULL,"
        + COLUMN_MAIN_SW_REV + " INTEGER NOT NULL,"
        + COLUMN_SUPP_SW_REV + " INTEGER NOT NULL,"
        + COLUMN_SERIAL_NR + " INTEGER NOT NULL,"
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
  
  
  public long addProductInformation(long userID, WeightScaleProductInformation measurement) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(6);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, measurement.getEstTimestamp());
      values.put(COLUMN_MAIN_SW_REV, measurement.getMainSoftwareRevision());
      values.put(COLUMN_SUPP_SW_REV, measurement.getSupplementalSoftwareRevision());
      values.put(COLUMN_SERIAL_NR, measurement.getSerialNumber());
      values.put(COLUMN_UPLOADED, measurement.isUploaded() ? VALUE_TRUE : VALUE_FALSE);
    
    try{
      long id = db.insertOrThrow(TABLE_NAME, null, values);
      
      for (WSProductInformationObserver o : observers) {
        o.onWSProductInformationDataAdded(id);
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
      
      for (WSProductInformationObserver observer : observers) {
        observer.onWSProductInformationDataUpdated(ids);
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

  
  
  public interface WSProductInformationObserver extends IRecordDataObserver {
    void onWSProductInformationDataAdded(long id);
    void onWSProductInformationDataUpdated(long[] ids);
  }
}
