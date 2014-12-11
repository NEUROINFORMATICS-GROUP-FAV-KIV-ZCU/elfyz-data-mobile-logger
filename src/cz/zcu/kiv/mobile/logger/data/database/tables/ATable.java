package cz.zcu.kiv.mobile.logger.data.database.tables;

import java.util.Calendar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public abstract class ATable {
  protected static final int VALUE_TRUE = 1;
  protected static final int VALUE_FALSE = 0;

  public static final String COLUMN_ID = "_id";
  
  protected static final String WHERE_ID = COLUMN_ID + " = ? ";
  
  
  public abstract void onCreate(SQLiteDatabase db);
  public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion);
  
  
  protected String getString(Cursor c, String columnName){
    return c.getString(c.getColumnIndex(columnName));
  }
  
  protected int getInt(Cursor c, String columnName){
    return c.getInt(c.getColumnIndex(columnName));
  }
  
  protected long getLong(Cursor c, String columnName){
    return c.getLong(c.getColumnIndex(columnName));
  }
  
  protected Calendar getCalendar(Cursor c, String columnName){
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(c.getLong(c.getColumnIndex(columnName)));
    return cal;
  }
  
  protected boolean getBoolean(Cursor c, String columnName){
    int value = c.getInt(c.getColumnIndex(columnName));
    return value == VALUE_TRUE;
  }
}
