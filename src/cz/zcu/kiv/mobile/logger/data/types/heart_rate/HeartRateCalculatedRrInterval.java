package cz.zcu.kiv.mobile.logger.data.types.heart_rate;

import java.math.BigDecimal;
import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.RrFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;


public class HeartRateCalculatedRrInterval {
  private long estTimestamp;
  private EnumSet<EventFlag> eventFlags;
  private BigDecimal calculatedRrInterval;
  private RrFlag rrFlag;
  private boolean uploaded;
  
  
  public HeartRateCalculatedRrInterval(long estTimestamp, EnumSet<EventFlag> eventFlags,
          BigDecimal calculatedRrInterval, RrFlag rrFlag, boolean uploaded) {
    this.estTimestamp = estTimestamp;
    this.eventFlags = eventFlags;
    this.calculatedRrInterval = calculatedRrInterval;
    this.rrFlag = rrFlag;
    this.uploaded = uploaded;
  }

  
  public long getEstTimestamp() {
    return estTimestamp;
  }

  public EnumSet<EventFlag> getEventFlags() {
    return eventFlags;
  }

  public BigDecimal getCalculatedRrInterval() {
    return calculatedRrInterval;
  }

  public RrFlag getRrFlag() {
    return rrFlag;
  }
  
  public boolean isUploaded() {
    return uploaded;
  }
}
