package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.WeightScaleProductInformation;


public class WeightScaleProductInformationTable extends ATable<WeightScaleProductInformationTable.WSProductInformationObserver> {
  private static final String TAG = WeightScaleProductInformationTable.class.getSimpleName();

  private static final String TABLE_NAME = "ws_prod_info";

  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_MAIN_SW_REV = "rate";
  public static final String COLUMN_SUPP_SW_REV = "count";
  public static final String COLUMN_SERIAL_NR = "data_state";
  
  
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
  
  
  public long addProductInformation(long userID, WeightScaleProductInformation measurement) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = new ContentValues(5);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TIME, measurement.getEstTimestamp());
      values.put(COLUMN_MAIN_SW_REV, measurement.getMainSoftwareRevision());
      values.put(COLUMN_SUPP_SW_REV, measurement.getSupplementalSoftwareRevision());
      values.put(COLUMN_SERIAL_NR, measurement.getSerialNumber());
    
    try{
      long id = db.insertOrThrow(TABLE_NAME, null, values);
      
      for (WSProductInformationObserver o : observers) {
        o.onWSProductInformation(id);
      }
      
      return id;
    }
    catch(SQLException e){
      throw new DatabaseException(e);
    }
  }

  
  
  public interface WSProductInformationObserver {
    void onWSProductInformation(long id);
  }
}
