package cz.zcu.kiv.mobile.logger.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.EntryNotFoundException;
import cz.zcu.kiv.mobile.logger.data.types.Gender;
import cz.zcu.kiv.mobile.logger.data.types.Profile;


public class ProfileTable extends ATable<ProfileTable.ProfileDataObserver> {
  private static final String TAG = ProfileTable.class.getSimpleName();

  public static final String TABLE_NAME = "profiles";

  public static final String COLUMN_PROFILE_NAME = "profile_name";
  public static final String COLUMN_EMAIL = "email";
  public static final String COLUMN_NAME = "name";
  public static final String COLUMN_SURNAME = "surname";
  public static final String COLUMN_BIRTH_DATE = "birth_date";
  public static final String COLUMN_GENDER = "gender";
  public static final String COLUMN_HEIGHT = "height";
  public static final String COLUMN_ACTIVITY_LEVEL = "activity_level";
  public static final String COLUMN_LIFETIME_ATHLETE = "lifetime_athlete";
  public static final String COLUMN_EEGBASE_PASSWORD = "eegbase_password";
  
  private static final String[] COLUMNS_PROFILES_ALL = new String[]{COLUMN_ID, COLUMN_PROFILE_NAME, COLUMN_EMAIL, COLUMN_NAME, COLUMN_SURNAME, COLUMN_BIRTH_DATE, COLUMN_GENDER, COLUMN_HEIGHT, COLUMN_ACTIVITY_LEVEL, COLUMN_LIFETIME_ATHLETE, COLUMN_EEGBASE_PASSWORD};
  private static final String[] COLUMNS_PROFILE_NAMES = new String[]{COLUMN_ID, COLUMN_PROFILE_NAME};

  private static final String WHERE_EMAIL = COLUMN_EMAIL + " = ? ";
  private static final String ORDER_PROFILES_ALL_ASC = COLUMN_PROFILE_NAME + " ASC";


  public ProfileTable(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }
  

  @Override
  void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
        + COLUMN_ID + " INTEGER PRIMARY KEY,"
        + COLUMN_PROFILE_NAME + " TEXT NOT NULL UNIQUE,"
        + COLUMN_EMAIL + " TEXT NULL UNIQUE,"
        + COLUMN_NAME + " TEXT NULL,"
        + COLUMN_SURNAME + " TEXT NULL,"
        + COLUMN_BIRTH_DATE + " INTEGER NOT NULL,"
        + COLUMN_GENDER + " TEXT NOT NULL,"
        + COLUMN_HEIGHT + " INTEGER NOT NULL,"
        + COLUMN_ACTIVITY_LEVEL + " INTEGER NOT NULL,"
        + COLUMN_LIFETIME_ATHLETE + " INTEGER NOT NULL,"
        + COLUMN_EEGBASE_PASSWORD + " TEXT NULL"
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
  
  
  public long createProfile(Profile profile) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    ContentValues values = getContentValues(profile);
    
    try{
      long id = db.insertOrThrow(TABLE_NAME, null, values);
      profile.setId(id);
      
      for (ProfileDataObserver o : observers) {
        o.onProfileAdded(id);
      }
      
      return id;
    }
    catch(Exception e){
      throw handleException(e);
    }
  }
  
  public void updateProfile(Profile profile) throws DatabaseException {
    SQLiteDatabase db = getDatabase();

    long profileID = profile.getId();
    ContentValues values = getContentValues(profile);

    try{
      db.update(TABLE_NAME, values, WHERE_ID, new String[]{String.valueOf(profileID)});
      
      for (ProfileDataObserver o : observers) {
        o.onProfileUpdated(profileID);
      }
    }
    catch(Exception e){
      throw handleException(e);
    }
  }


  public void deleteProfile(long profileID) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    try{
      db.delete(TABLE_NAME, WHERE_ID, new String[]{String.valueOf(profileID)});
      
      for (ProfileDataObserver o : observers) {
        o.onProfileDeleted(profileID);
      }
    }
    catch(Exception e){
      throw handleException(e);
    }
  }

  public Cursor getProfileNames() throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    try{
      return db.query(TABLE_NAME, COLUMNS_PROFILE_NAMES, null, null, null, null, ORDER_PROFILES_ALL_ASC);
    }
    catch(Exception e){
      throw handleException(e);
    }
  }
  
  public Profile getProfile(long profileID) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    Cursor c = null;
    try{
      c = db.query(TABLE_NAME, COLUMNS_PROFILES_ALL, WHERE_ID, new String[]{String.valueOf(profileID)}, null, null, null);
      
      if(c.getCount() == 1){
        c.moveToFirst();
        
        return readProfile(c);
      }
      else {
        throw new EntryNotFoundException("Failed to get entry with given ID: ID=" + profileID + ", row count=" + c.getCount());
      }
    }
    catch(Exception e){
      throw handleException(e);
    }
  }


  public Profile getProfile(String email) throws DatabaseException {
    SQLiteDatabase db = getDatabase();
    
    Cursor c = null;
    try{
      c = db.query(TABLE_NAME, COLUMNS_PROFILES_ALL, WHERE_EMAIL, new String[]{email}, null, null, null);
      
      if(c.getCount() == 1) {
        c.moveToFirst();
        
        return readProfile(c);
      }
      else {
        throw new EntryNotFoundException("Failed to get entry with given email: email=" + email + ", row count=" + c.getCount());
      }
    }
    catch(Exception e){
      throw handleException(e);
    }
  }

  
  private Profile readProfile(Cursor c) {
    return new Profile(
        getLong(c, COLUMN_ID),
        getString(c, COLUMN_PROFILE_NAME),
        getString(c, COLUMN_EMAIL),
        getString(c, COLUMN_NAME),
        getString(c, COLUMN_SURNAME),
        getCalendar(c, COLUMN_BIRTH_DATE),
        Gender.fromLetter(getString(c, COLUMN_GENDER)),
        getInt(c, COLUMN_HEIGHT),
        getInt(c, COLUMN_ACTIVITY_LEVEL),
        getBoolean(c, COLUMN_LIFETIME_ATHLETE),
        getString(c, COLUMN_EEGBASE_PASSWORD));
  }

  private ContentValues getContentValues(Profile profile) {
    ContentValues values = new ContentValues(10);
      values.put(COLUMN_PROFILE_NAME, profile.getProfileName());
      values.put(COLUMN_EMAIL, profile.getEmail());
      values.put(COLUMN_NAME, profile.getName());
      values.put(COLUMN_SURNAME, profile.getSurname());
      values.put(COLUMN_BIRTH_DATE, profile.getBirthDate().getTimeInMillis());
      values.put(COLUMN_GENDER, profile.getGender().getLetter());
      values.put(COLUMN_HEIGHT, profile.getHeight());
      values.put(COLUMN_ACTIVITY_LEVEL, profile.getActivityLevel());
      values.put(COLUMN_LIFETIME_ATHLETE, profile.isLifetimeAthlete() ? VALUE_TRUE : VALUE_FALSE);
      values.put(COLUMN_EEGBASE_PASSWORD, profile.getEegbasePassword());
    return values;
  }
  
  
  
  public interface ProfileDataObserver {
    void onProfileAdded(long id);
    void onProfileUpdated(long profileID);
    void onProfileDeleted(long profileID);
  }
}
