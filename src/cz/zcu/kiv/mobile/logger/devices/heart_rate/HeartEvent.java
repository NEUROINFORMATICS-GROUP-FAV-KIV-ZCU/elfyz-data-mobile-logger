package cz.zcu.kiv.mobile.logger.devices.heart_rate;

import java.math.BigDecimal;

import android.os.Parcel;
import android.os.Parcelable;

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.DataState;


public class HeartEvent implements Parcelable {
  private long estTimestamp;
  private int computedHeartRate;
  private long heartBeatCount;
  private BigDecimal heartBeatEventTime;
  private DataState dataState;
  
  
  public HeartEvent() {}
  
  public HeartEvent(long estTimestamp,
      int computedHeartRate, long heartBeatCount,
      BigDecimal heartBeatEventTime, DataState dataState) {
    this.estTimestamp = estTimestamp;
    this.computedHeartRate = computedHeartRate;
    this.heartBeatCount = heartBeatCount;
    this.heartBeatEventTime = heartBeatEventTime;
    this.dataState = dataState;
  }
  
  public HeartEvent(Parcel source) {
    readFromParcel(source);
  }
  
  
  public long getEstTimestamp() {
    return estTimestamp;
  }
  public void setEstTimestamp(long estTimestamp) {
    this.estTimestamp = estTimestamp;
  }
  public int getComputedHeartRate() {
    return computedHeartRate;
  }
  public void setComputedHeartRate(int computedHeartRate) {
    this.computedHeartRate = computedHeartRate;
  }
  public long getHeartBeatCount() {
    return heartBeatCount;
  }
  public void setHeartBeatCount(long heartBeatCount) {
    this.heartBeatCount = heartBeatCount;
  }
  public BigDecimal getHeartBeatEventTime() {
    return heartBeatEventTime;
  }
  public void setHeartBeatEventTime(BigDecimal heartBeatEventTime) {
    this.heartBeatEventTime = heartBeatEventTime;
  }
  public DataState getDataState() {
    return dataState;
  }
  public void setDataState(DataState dataState) {
    this.dataState = dataState;
  }

  
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(estTimestamp);
    dest.writeInt(computedHeartRate);
    dest.writeLong(heartBeatCount);
    dest.writeString(heartBeatEventTime.toString());
    dest.writeSerializable(dataState);
  }
  
  public void readFromParcel(Parcel source) {
    estTimestamp = source.readLong();
    computedHeartRate = source.readInt();
    heartBeatCount = source.readLong();
    heartBeatEventTime = new BigDecimal(source.readString());
    dataState = (DataState) source.readSerializable();
  }
  
  
  public static final Parcelable.Creator<HeartEvent> CREATOR = new Creator<HeartEvent>() {
    @Override
    public HeartEvent[] newArray(int size) {
      return new HeartEvent[size];
    }
    @Override
    public HeartEvent createFromParcel(Parcel source) {
      return new HeartEvent(source);
    }
  };
}
