package cz.zcu.kiv.mobile.logger.data.database.tables;

import java.util.Calendar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DuplicateEntryException;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.EntryNotFoundException;
import cz.zcu.kiv.mobile.logger.data.types.Gender;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.utils.CloseUtil;


public class ProfileTable extends ATable {
  private static final String TAG = ProfileTable.class.getSimpleName();

  public static final String TABLE_NAME = "profiles";

  public static final String COLUMN_PROFILE_NAME = "name";
  public static final String COLUMN_BIRTH_DATE = "birth_date";
  public static final String COLUMN_GENDER = "gender";
  public static final String COLUMN_HEIGHT = "height";
  public static final String COLUMN_ACTIVITY_LEVEL = "activity_level";
  public static final String COLUMN_LIFETIME_ATHLETE = "lifetime_athlete";
  
  private static final String[] COLUMNS_PROFILES_ALL = new String[]{COLUMN_ID, COLUMN_PROFILE_NAME, COLUMN_BIRTH_DATE, COLUMN_GENDER, COLUMN_HEIGHT, COLUMN_ACTIVITY_LEVEL, COLUMN_LIFETIME_ATHLETE};
  private static final String[] COLUMNS_PROFILE_NAMES = new String[]{COLUMN_ID, COLUMN_PROFILE_NAME};
    
  private static final String ORDER_PROFILES_ALL_ASC = COLUMN_PROFILE_NAME + " ASC";

  
  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_PROFILE_NAME + " TEXT NOT NULL UNIQUE,"
        + COLUMN_BIRTH_DATE + " INTEGER NOT NULL,"
        + COLUMN_GENDER + " TEXT NOT NULL,"
        + COLUMN_HEIGHT + " INTEGER NOT NULL,"
        + COLUMN_ACTIVITY_LEVEL + " INTEGER NOT NULL,"
        + COLUMN_LIFETIME_ATHLETE + " INTEGER NOT NULL"
        + ");");
  }
  
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
    int upgradeVersion = oldVersion;

    if(upgradeVersion != currentVersion){
      Log.d(TAG, "Wasn't able to upgrade the database. Wiping and rebuilding...");

      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

      onCreate(db);
    }
  }
  
  
  public long createProfile(SQLiteDatabase db, String profileName, Calendar birthDate, Gender gender, int height, int activityLevel, boolean lifetimeAthlete) throws DatabaseException {
    ContentValues values = new ContentValues(1);
    values.put(COLUMN_PROFILE_NAME, profileName);
    values.put(COLUMN_BIRTH_DATE, birthDate.getTimeInMillis());
    values.put(COLUMN_GENDER, gender.getLetter());
    values.put(COLUMN_HEIGHT, height);
    values.put(COLUMN_ACTIVITY_LEVEL, activityLevel);
    values.put(COLUMN_LIFETIME_ATHLETE, lifetimeAthlete ? VALUE_TRUE : VALUE_FALSE);
    
    try{
      return db.insertOrThrow(TABLE_NAME, null, values);
    }
    catch(Exception e){
      if(e instanceof SQLiteConstraintException && e.getMessage().equals("column name is not unique (code 19)"))
        throw new DuplicateEntryException(e);
      throw new DatabaseException(e);
    }
  }

  public void deleteProfile(SQLiteDatabase db, long profileID) throws DatabaseException {
    String[] idArg = new String[]{String.valueOf(profileID)};
    
    try{
      db.delete(TABLE_NAME, WHERE_ID, idArg);
    }
    catch(Exception e){
      throw new DatabaseException(e);
    }
  }

  public Cursor getProfileNames(SQLiteDatabase db) throws DatabaseException {
    try{
      return db.query(TABLE_NAME, COLUMNS_PROFILE_NAMES, null, null, null, null, ORDER_PROFILES_ALL_ASC);
    }
    catch(Exception e){
      throw new DatabaseException(e);
    }
  }
  
  public Profile getProfile(SQLiteDatabase db, long profileID) throws DatabaseException {
    Cursor c = null;
    try{
      c = db.query(TABLE_NAME, COLUMNS_PROFILES_ALL, WHERE_ID, new String[]{String.valueOf(profileID)}, null, null, null);
      
      if(c.getCount() == 1){
        c.moveToFirst();
        
        return new Profile(
            profileID,
            getString(c, COLUMN_PROFILE_NAME),
            getCalendar(c, COLUMN_BIRTH_DATE),
            Gender.fromLetter(getString(c, COLUMN_GENDER)),
            getInt(c, COLUMN_HEIGHT),
            getInt(c, COLUMN_ACTIVITY_LEVEL),
            getBoolean(c, COLUMN_LIFETIME_ATHLETE));
      }
      else {
        throw new EntryNotFoundException("Failed to get entry with given ID: ID=" + profileID + ", row count=" + c.getCount());
      }
    }
    catch(Exception e){
      if(e instanceof DatabaseException)
        throw (DatabaseException) e;
      throw new DatabaseException(e);
    }
    finally{
      CloseUtil.close(c);
    }
  }
}
