package cz.zcu.kiv.mobile.logger.data.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;


public abstract class ARecordTable<T_Observer extends ARecordTable.IRecordDataObserver> extends ATable<T_Observer> {
  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_UPLOADED = "uploaded";

  protected static final String WHERE_USER_ID = COLUMN_USER_ID + " = ? ";
  

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
  
  
  protected abstract String getTableName();
  
  
  public interface IRecordDataObserver {
    void onClear();
  }
}
