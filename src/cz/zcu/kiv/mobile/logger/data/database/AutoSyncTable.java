package cz.zcu.kiv.mobile.logger.data.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.EntryNotFoundException;
import cz.zcu.kiv.mobile.logger.data.types.AutoSync;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public class AutoSyncTable extends ATable<AutoSyncTable.AutoSyncDataObserver> {
  private static final String TAG = AutoSyncTable.class.getSimpleName();

  public static final String TABLE_NAME = "auto_sync";

  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_TABLE_ID = "table_id";
  public static final String COLUMN_EXPERIMENT_ID = "experiment_id";
  public static final String COLUMN_EXPERIMENT_NAME = "experiment_name";
  
  private static final String[] COLUMNS_ALL = new String[]{COLUMN_ID, COLUMN_USER_ID, COLUMN_TABLE_ID, COLUMN_EXPERIMENT_ID, COLUMN_EXPERIMENT_NAME};

  protected static final String WHERE_RECORD = COLUMN_USER_ID + " = ? AND " + COLUMN_TABLE_ID + " = ? ";
  protected static final String WHERE_USER_ID = COLUMN_USER_ID + " = ? ";
  

  public AutoSyncTable(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }
  

  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_USER_ID + " INTEGER NOT NULL,"
        + COLUMN_TABLE_ID + " INTEGER NULL,"
        + COLUMN_EXPERIMENT_ID + " INTEGER NULL UNIQUE,"
        + COLUMN_EXPERIMENT_NAME + " TEXT NULL,"
        + "UNIQUE (" + COLUMN_USER_ID + "," + COLUMN_TABLE_ID + "), "
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
  
  
  public long addAutoSync(long userID, AutoSync autosync) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = getContentValues(userID, autosync);
    
    try{
      long id = db.insertOrThrow(TABLE_NAME, null, values);
      
      for (AutoSyncDataObserver o : observers) {
        o.onRecordAdded(id);
      }
      
      return id;
    }
    catch(Exception e){
      throw handleException(e);
    }
  }
  
  public void deleteAutoSync(long userID, int tableID) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    String[] idArgs = new String[]{String.valueOf(userID), String.valueOf(tableID)};
    
    try{
      db.delete(TABLE_NAME, WHERE_RECORD, idArgs);
      
      for (AutoSyncDataObserver o : observers) {
        o.onRecordDeleted(userID, tableID);
      }
    }
    catch(Exception e){
      throw handleException(e);
    }
  }

  public List<AutoSync> getRecords(long userID) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    List<AutoSync> list = new ArrayList<AutoSync>();
    
    Cursor c = null;
    try{
      String[] selectionArgs = new String[]{ String.valueOf(userID) };
      c = db.query(TABLE_NAME, COLUMNS_ALL, WHERE_USER_ID, selectionArgs, null, null, null);
      
      int iTableID = c.getColumnIndex(COLUMN_TABLE_ID);
      int iExpID = c.getColumnIndex(COLUMN_EXPERIMENT_ID);
      int iExpName = c.getColumnIndex(COLUMN_EXPERIMENT_NAME);
      
      while(c.moveToNext()) {
        list.add(new AutoSync(
            c.getInt(iTableID),
            c.getString(iExpID),
            c.getString(iExpName)));
      }
      
      return list;
    }
    catch(Exception e){
      throw handleException(e);
    }
    finally {
      CloseUtils.close(c);
    }
  }
  
  public AutoSync getAutoSync(long userID, int tableID) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    Cursor c = null;
    try{
      String[] idArgs = new String[]{String.valueOf(userID), String.valueOf(tableID)};
      c = db.query(TABLE_NAME, COLUMNS_ALL, WHERE_RECORD, idArgs, null, null, null);
      
      if(c.getCount() == 1){
        c.moveToFirst();
        
        return new AutoSync(
            getInt(c, COLUMN_TABLE_ID),
            getString(c, COLUMN_EXPERIMENT_ID),
            getString(c, COLUMN_EXPERIMENT_NAME));
      }
      else {
        throw new EntryNotFoundException("Failed to get entry with given ID: userID=" + userID + ", tableID=" + tableID + ", row count=" + c.getCount());
      }
    }
    catch(Exception e){
      throw handleException(e);
    }
  }

  
  private ContentValues getContentValues(long userID, AutoSync autosync) {
    ContentValues values = new ContentValues(4);
      values.put(COLUMN_USER_ID, userID);
      values.put(COLUMN_TABLE_ID, autosync.getTableID());
      values.put(COLUMN_EXPERIMENT_ID, autosync.getExperimentID());
      values.put(COLUMN_EXPERIMENT_NAME, autosync.getExperimentName());
    return values;
  }
  
  
  
  public interface AutoSyncDataObserver {
    void onRecordAdded(long id);
    void onRecordDeleted(long userID, int tableID);
  }
}
