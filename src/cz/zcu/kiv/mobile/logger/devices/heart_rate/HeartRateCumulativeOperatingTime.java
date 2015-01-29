package cz.zcu.kiv.mobile.logger.devices.heart_rate;

import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;


public class HeartRateCumulativeOperatingTime {
  private long estTimestamp;
  private EnumSet<EventFlag> eventFlags;
  private long cumulativeOperatingTime;
  
  
  public HeartRateCumulativeOperatingTime(long estTimestamp, EnumSet<EventFlag> eventFlags, long cumulativeOperatingTime) {
    this.estTimestamp = estTimestamp;
    this.eventFlags = eventFlags;
    this.cumulativeOperatingTime = cumulativeOperatingTime;
  }

  
  public long getEstTimestamp() {
    return estTimestamp;
  }

  public EnumSet<EventFlag> getEventFlags() {
    return eventFlags;
  }

  public long getCumulativeOperatingTime() {
    return cumulativeOperatingTime;
  }
}
