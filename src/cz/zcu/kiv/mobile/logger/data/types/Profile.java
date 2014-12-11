package cz.zcu.kiv.mobile.logger.data.types;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;


public class Profile implements Parcelable {
  private long id;
  private String profileName;
  private Calendar birthDate;
  private Gender gender;
  private int height;
  private int activityLevel;
  private boolean lifetimeAthlete;
  
  
  public Profile() {}
  
  public Profile(long id, String profileName, Calendar birthDate, Gender gender,
      int height, int activityLevel, boolean lifetimeAthlete) {
    this.id = id;
    this.profileName = profileName;
    this.birthDate = birthDate;
    this.gender = gender;
    this.height = height;
    this.activityLevel = activityLevel;
    this.lifetimeAthlete = lifetimeAthlete;
  }
  
  public Profile(Parcel source) {
    readFromParcel(source);
  }

  
  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }
  public String getProfileName() {
    return profileName;
  }
  public void setProfileName(String profileName) {
    this.profileName = profileName;
  }
  public Calendar getBirthDate() {
    return birthDate;
  }
  public void setBirthDate(Calendar birthDate) {
    this.birthDate = birthDate;
  }
  public Gender getGender() {
    return gender;
  }
  public void setGender(Gender gender) {
    this.gender = gender;
  }
  public int getHeight() {
    return height;
  }
  public void setHeight(int height) {
    this.height = height;
  }
  public int getActivityLevel() {
    return activityLevel;
  }
  public void setActivityLevel(int activityLevel) {
    this.activityLevel = activityLevel;
  }
  public boolean isLifetimeAthlete() {
    return lifetimeAthlete;
  }
  public void setLifetimeAthlete(boolean lifetimeAthlete) {
    this.lifetimeAthlete = lifetimeAthlete;
  }
  
  
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(id);
    dest.writeString(profileName);
    dest.writeLong(birthDate.getTimeInMillis());
    dest.writeString(gender.getLetter());
    dest.writeInt(height);
    dest.writeInt(activityLevel);
    dest.writeInt(lifetimeAthlete ? 1 : 0);
  }
  
  public void readFromParcel(Parcel source) {
    id = source.readLong();
    profileName = source.readString();
    birthDate = Calendar.getInstance();
      birthDate.setTimeInMillis(source.readLong());
    gender = Gender.fromLetter(source.readString());
    height = source.readInt();
    activityLevel = source.readInt();
    lifetimeAthlete = source.readInt() == 1;
  }
  
  
  public static final Parcelable.Creator<Profile> CREATOR = new Creator<Profile>() {
    @Override
    public Profile[] newArray(int size) {
      return new Profile[size];
    }
    @Override
    public Profile createFromParcel(Parcel source) {
      return new Profile(source);
    }
  };


  public int calculateAge() {
    Calendar age = Calendar.getInstance();
    age.add(Calendar.YEAR, -birthDate.get(Calendar.YEAR));
    age.add(Calendar.MONTH, -birthDate.get(Calendar.MONTH));
    age.add(Calendar.DAY_OF_MONTH, -birthDate.get(Calendar.DAY_OF_MONTH));
    
    return age.get(Calendar.YEAR);
  }
}
