package cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.os.Parcel;
import android.os.Parcelable;


public class BloodPressureMeasurement implements Parcelable {
  private Calendar time;
  private int systolicPressure;
  private int diastolicPressure;
  private int meanPressure;
  private int heartRate;
  
  
  public BloodPressureMeasurement() {}
  
  public BloodPressureMeasurement(Calendar time, int systolicPressure,
      int diastolicPressure, int meanPressure, int heartRate) {
    this.time = time;
    this.systolicPressure = systolicPressure;
    this.diastolicPressure = diastolicPressure;
    this.meanPressure = meanPressure;
    this.heartRate = heartRate;
  }
  
  public BloodPressureMeasurement(Parcel source) {
    readFromParcel(source);
  }
  

  public Calendar getTime() {
    return time;
  }
  public void setTime(Calendar time) {
    this.time = time;
  }
  public int getSystolicPressure() {
    return systolicPressure;
  }
  public void setSystolicPressure(int systolicPressure) {
    this.systolicPressure = systolicPressure;
  }
  public int getDiastolicPressure() {
    return diastolicPressure;
  }
  public void setDiastolicPressure(int diastolicPressure) {
    this.diastolicPressure = diastolicPressure;
  }
  public int getMeanPressure() {
    return meanPressure;
  }
  public void setMeanPressure(int meanPressure) {
    this.meanPressure = meanPressure;
  }
  public int getHeartRate() {
    return heartRate;
  }
  public void setHeartRate(int heartRate) {
    this.heartRate = heartRate;
  }

  
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(time.getTimeZone().getID());
    dest.writeLong(time.getTimeInMillis());
    dest.writeInt(systolicPressure);
    dest.writeInt(diastolicPressure);
    dest.writeInt(meanPressure);
    dest.writeInt(heartRate);
  }
  
  public void readFromParcel(Parcel source) {
    time = new GregorianCalendar();
    time.setTimeZone(TimeZone.getTimeZone(source.readString()));
    time.setTimeInMillis(source.readLong());
    systolicPressure = source.readInt();
    diastolicPressure = source.readInt();
    meanPressure = source.readInt();
    heartRate = source.readInt();
  }
  
  
  public static final Parcelable.Creator<BloodPressureMeasurement> CREATOR = new Creator<BloodPressureMeasurement>() {
    @Override
    public BloodPressureMeasurement[] newArray(int size) {
      return new BloodPressureMeasurement[size];
    }
    @Override
    public BloodPressureMeasurement createFromParcel(Parcel source) {
      return new BloodPressureMeasurement(source);
    }
  };
}
