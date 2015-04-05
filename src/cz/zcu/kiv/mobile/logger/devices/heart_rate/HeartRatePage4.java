package cz.zcu.kiv.mobile.logger.devices.heart_rate;

import java.math.BigDecimal;
import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;


public class HeartRatePage4 {
  private long estTimestamp;
  private EnumSet<EventFlag> eventFlags;
  private int manufacturerSpecificByte;
  private BigDecimal previousHeartBeatEventTime;
  private boolean uploaded;
  
  
  public HeartRatePage4(long estTimestamp, EnumSet<EventFlag> eventFlags, int manufacturerSpecificByte, BigDecimal previousHeartBeatEventTime, boolean uploaded) {
    this.estTimestamp = estTimestamp;
    this.eventFlags = eventFlags;
    this.manufacturerSpecificByte = manufacturerSpecificByte;
    this.previousHeartBeatEventTime = previousHeartBeatEventTime;
    this.uploaded = uploaded;
  }


  public long getEstTimestamp() {
    return estTimestamp;
  }

  public EnumSet<EventFlag> getEventFlags() {
    return eventFlags;
  }

  public int getManufacturerSpecificByte() {
    return manufacturerSpecificByte;
  }

  public BigDecimal getPreviousHeartBeatEventTime() {
    return previousHeartBeatEventTime;
  }
  
  public boolean isUploaded() {
    return uploaded;
  }
}
