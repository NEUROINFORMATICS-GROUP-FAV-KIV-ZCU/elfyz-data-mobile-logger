package cz.zcu.kiv.mobile.logger.devices.weight_scale;

import java.math.BigDecimal;
import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.WeightScaleRequestStatus;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;


public class WeightScaleAdvancedMeasurement {
  private long estTimestamp;
  private EnumSet<EventFlag> eventFlags;
  private WeightScaleRequestStatus status;
  private BigDecimal activeMetabolicRate;
  private BigDecimal basalMetabolicRate;
  private BigDecimal bodyFatPercentage;
  private BigDecimal bodyWeight;
  private BigDecimal boneMass;
  private BigDecimal hydrationPercentage;
  private BigDecimal muscleMass;
  private boolean uploaded;
  

  public WeightScaleAdvancedMeasurement(long estTimestamp,
      EnumSet<EventFlag> eventFlags, WeightScaleRequestStatus status,
      BigDecimal activeMetabolicRate, BigDecimal basalMetabolicRate,
      BigDecimal bodyFatPercentage, BigDecimal bodyWeight, BigDecimal boneMass,
      BigDecimal hydrationPercentage, BigDecimal muscleMass, boolean uploaded) {
    this.estTimestamp = estTimestamp;
    this.eventFlags = eventFlags;
    this.status = status;
    this.activeMetabolicRate = activeMetabolicRate;
    this.basalMetabolicRate = basalMetabolicRate;
    this.bodyFatPercentage = bodyFatPercentage;
    this.bodyWeight = bodyWeight;
    this.boneMass = boneMass;
    this.hydrationPercentage = hydrationPercentage;
    this.muscleMass = muscleMass;
    this.uploaded = uploaded;
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

  public BigDecimal getActiveMetabolicRate() {
    return activeMetabolicRate;
  }

  public BigDecimal getBasalMetabolicRate() {
    return basalMetabolicRate;
  }

  public BigDecimal getBodyFatPercentage() {
    return bodyFatPercentage;
  }

  public BigDecimal getBodyWeight() {
    return bodyWeight;
  }

  public BigDecimal getBoneMass() {
    return boneMass;
  }

  public BigDecimal getHydrationPercentage() {
    return hydrationPercentage;
  }

  public BigDecimal getMuscleMass() {
    return muscleMass;
  }
  
  public boolean isUploaded() {
    return uploaded;
  }
}
