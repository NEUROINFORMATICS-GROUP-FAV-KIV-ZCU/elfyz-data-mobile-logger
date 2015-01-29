package cz.zcu.kiv.mobile.logger.devices.weight_scale;

import java.math.BigDecimal;
import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.WeightScaleRequestStatus;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;


public class WeightScaleBasicMeasurement {
  private long estTimestamp;
  private EnumSet<EventFlag> eventFlags;
  private WeightScaleRequestStatus status;
  private BigDecimal bodyWeight;
  
  
  public WeightScaleBasicMeasurement(long estTimestamp, EnumSet<EventFlag> eventFlags,
          WeightScaleRequestStatus status, BigDecimal bodyWeight) {
    this.estTimestamp = estTimestamp;
    this.eventFlags = eventFlags;
    this.status = status;
    this.bodyWeight = bodyWeight;
  }


  public long getEstTimestamp() {
    return estTimestamp;
  }

  public EnumSet<EventFlag> getEventFlags() {
    return eventFlags;
  }

  public WeightScaleRequestStatus getStatus() {
    return status;
  }

  public BigDecimal getBodyWeight() {
    return bodyWeight;
  }
}
