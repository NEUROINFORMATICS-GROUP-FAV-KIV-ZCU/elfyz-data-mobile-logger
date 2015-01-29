package cz.zcu.kiv.mobile.logger.devices.heart_rate;

import java.math.BigDecimal;
import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.DataState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;


public class HeartRateMeasurement {
  private long estTimestamp;
  private EnumSet<EventFlag> eventFlags;
  private int computedHeartRate;
  private long heartBeatCount;
  private BigDecimal heartBeatEventTime;
  private DataState dataState;
  
  
  public HeartRateMeasurement(long estTimestamp, EnumSet<EventFlag> eventFlags, int computedHeartRate,
      long heartBeatCount, BigDecimal heartBeatEventTime, DataState dataState) {
    this.estTimestamp = estTimestamp;
    this.eventFlags = eventFlags;
    this.computedHeartRate = computedHeartRate;
    this.heartBeatCount = heartBeatCount;
    this.heartBeatEventTime = heartBeatEventTime;
    this.dataState = dataState;
  }
  
  
  public long getEstTimestamp() {
    return estTimestamp;
  }
  public EnumSet<EventFlag> getEventFlags() {
    return eventFlags;
  }
  public int getComputedHeartRate() {
    return computedHeartRate;
  }
  public long getHeartBeatCount() {
    return heartBeatCount;
  }
  public BigDecimal getHeartBeatEventTime() {
    return heartBeatEventTime;
  }
  public DataState getDataState() {
    return dataState;
  }
}
