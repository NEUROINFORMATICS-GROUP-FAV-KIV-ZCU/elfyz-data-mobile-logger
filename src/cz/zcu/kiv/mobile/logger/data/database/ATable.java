package cz.zcu.kiv.mobile.logger.data.database;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DuplicateEntryException;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


public abstract class ATable<T_Observer> {
  public static final int VALUE_TRUE = 1;
  public static final int VALUE_FALSE = 0;

  public static final String COLUMN_ID = "_id";
  
  protected static final String WHERE_ID = COLUMN_ID + " = ? ";
  
  private static final Pattern PATTERN_DUPLICATE = Pattern.compile("column(s)? (.*) (is|are) not unique \\(code 19\\)");
  
  protected Set<T_Observer> observers = new HashSet<T_Observer>();
  private SQLiteOpenHelper openHelper;
  
  
  public ATable(SQLiteOpenHelper openHelper) {
    this.openHelper = openHelper;
  }

  
  //friendly access to provide only other methods to public
  abstract void onCreate(SQLiteDatabase db);
  abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion);
  
  
  public void addObserver(T_Observer observer) {
    observers.add(observer);
  }
  
  public void removeObserver(T_Observer observer) {
    observers.remove(observer);
  }
  
  protected SQLiteDatabase getDatabase() throws DatabaseException {
    try {
      return openHelper.getWritableDatabase();
    }
    catch (SQLiteException e) {
      throw new DatabaseException("Failed to open database.", e);
    }
  }
  
  public DatabaseException handleException(Exception e)  {
    if(e instanceof SQLiteConstraintException) {
      Matcher matcher = PATTERN_DUPLICATE.matcher(e.getMessage());
      if(matcher.matches()) {
        return new DuplicateEntryException(matcher.group(2), e);
      }
    }
    if(e instanceof DatabaseException) {
      return (DatabaseException) e;
    }
    return new DatabaseException(e);
  }
  
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

  protected String[] toStringArray(long[] longs) {
    String[] strings = new String[longs.length];
    for (int i = 0; i < longs.length; i++) {
      strings[i] = String.valueOf(longs[i]);
    }
    return strings;
  }


  protected String assemblePlaceholders(int length) {
    StringBuilder sb = new StringBuilder(length * 2 + 2);
    sb.append("(?");
    for (int i = 1; i < length; i++) { //from 1 because one placeholder is already added
      sb.append(",?");
    }
    sb.append(") ");
    return sb.toString();
  }
}
