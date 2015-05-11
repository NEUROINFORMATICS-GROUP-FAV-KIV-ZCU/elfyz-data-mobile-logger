package cz.zcu.kiv.mobile.logger.data.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public abstract class ARecordTable<T_Observer extends ARecordTable.IRecordDataObserver> extends ATable<T_Observer> {
  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_UPLOADED = "uploaded";
  
  protected static final String WHERE_USER_ID = COLUMN_USER_ID + " = ? ";
  protected static final String WHERE_UPLOADED = COLUMN_USER_ID + " = ? AND " + COLUMN_UPLOADED + " = ?";
  protected static final String WHERE_IDS_IN_ = COLUMN_ID + " IN ";
  protected static final String WHERE_USER_ID_AND_DATES = COLUMN_USER_ID + " = ? AND " + COLUMN_TIME + " >= ? AND " + COLUMN_TIME + " <= ? ";
  
  private int tableID;
  

  public ARecordTable(SQLiteOpenHelper openHelper, int tableID) {
    super(openHelper);
    this.tableID = tableID;
  }

  
  public long[] getNotUploadedIDs(long userID, int count) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    long[] ids = null;
    
    Cursor c = null;
    try {
      String[] selArgs = new String[] { String.valueOf(userID), String.valueOf(VALUE_FALSE)};
      String limit = (count > 0) ? String.valueOf(count) : null;
      
      c = db.query(getTableName(), new String[]{COLUMN_ID}, WHERE_UPLOADED, selArgs, null, null, null, limit);
      
      int iID = c.getColumnIndex(COLUMN_ID);
      int size = c.getCount();
      ids = new long[size];
      
      for (int i = 0; i < size; i++) {
        c.moveToNext();
        ids[i] = c.getLong(iID);
      }
      
      return ids;
    }
    catch (Exception e) {
      throw new DatabaseException(e);
    }
    finally {
      CloseUtils.close(c);
    }
  }

  public void deleteRecord(long id) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    try {
      db.delete(getTableName(), WHERE_ID, new String[]{String.valueOf(id)});
      
      for (IRecordDataObserver o : observers) {
        o.onRecordsDeleted(id);
      }
    }
    catch (Exception e) {
      throw new DatabaseException(e);
    }
  }

  public void deleteRecords(long[] ids) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    try {
      db.delete(getTableName(), WHERE_IDS_IN_ + assemblePlaceholders(ids.length), toStringArray(ids));
      
      for (IRecordDataObserver o : observers) {
        o.onRecordsDeleted(ids);
      }
    }
    catch(Exception e) {
      throw new DatabaseException(e);
    }
  }

  public void deleteUploadedRecords(long userID) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    try {
      db.delete(getTableName(), WHERE_UPLOADED, new String[]{String.valueOf(userID), String.valueOf(VALUE_TRUE)});
      
      for (IRecordDataObserver o : observers) {
        o.onRecordsDeleted(-1L);
      }
    }
    catch(Exception e) {
      throw new DatabaseException(e);
    }
  }

  public void deleteAllRecords(long userID) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    try {
      db.delete(getTableName(), WHERE_USER_ID, new String[]{String.valueOf(userID)});
      
      for (IRecordDataObserver o : observers) {
        o.onClear();
      }
    }
    catch(Exception e) {
      throw new DatabaseException(e);
    }
  }

  
  public int getTableID() {
    return tableID;
  }
  
  
  protected abstract String getTableName();
  public abstract void setUploaded(long[] ids) throws DatabaseException;
  
  
  public interface IRecordDataObserver {
    void onClear();
    void onRecordsDeleted(long... ids);
  }
}
