package cz.zcu.kiv.mobile.logger.data.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public abstract class ARecordTable<T_Observer extends ARecordTable.IRecordDataObserver> extends ATable<T_Observer> {
  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_UPLOADED = "uploaded";
  
  protected static final String WHERE_USER_ID = COLUMN_USER_ID + " = ? ";
  protected static final String WHERE_UPLOADED = COLUMN_USER_ID + " = ? AND " + COLUMN_UPLOADED + " = ?";
  

  public ARecordTable(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }


  public void clearForProfile(long userID) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    try{
      db.delete(getTableName(), WHERE_USER_ID, new String[]{String.valueOf(userID)});
      
      for (IRecordDataObserver observer : observers) {
        observer.onClear();
      }
    }
    catch(Exception e){
      throw handleException(e);
    }
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
  
  
  protected abstract String getTableName();
  
  public abstract void setUploaded(long[] ids) throws DatabaseException;
  
  
  public interface IRecordDataObserver {
    void onClear();
  }
}
