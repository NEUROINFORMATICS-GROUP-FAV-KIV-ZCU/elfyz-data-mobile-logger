package cz.zcu.kiv.mobile.logger.data.types.weight_scale;

import java.math.BigDecimal;
import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.defines.BatteryStatus;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;


public class WeightScaleBatteryStatus {
  private long estTimestamp;
  private EnumSet<EventFlag> eventFlags;
  private long cumulativeOperatingTime;
  private BigDecimal batteryVoltage;
  private BatteryStatus batteryStatus;
  private int cumulativeOperatingTimeResolution;
  private int numberOfBatteries;
  private int batteryIdentifier;
  private boolean uploaded;


  public WeightScaleBatteryStatus(long estTimestamp, EnumSet<EventFlag> eventFlags, long cumulativeOperatingTime,
      BigDecimal batteryVoltage, BatteryStatus batteryStatus, int cumulativeOperatingTimeResolution, int numberOfBatteries,
      int batteryIdentifier, boolean uploaded) {
    this.estTimestamp = estTimestamp;
    this.eventFlags = eventFlags;
    this.cumulativeOperatingTime = cumulativeOperatingTime;
    this.batteryVoltage = batteryVoltage;
    this.batteryStatus = batteryStatus;
    this.cumulativeOperatingTimeResolution = cumulativeOperatingTimeResolution;
    this.numberOfBatteries = numberOfBatteries;
    this.batteryIdentifier = batteryIdentifier;
    this.uploaded = uploaded;
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

  public BigDecimal getBatteryVoltage() {
    return batteryVoltage;
  }

  public BatteryStatus getBatteryStatus() {
    return batteryStatus;
  }

  public int getCumulativeOperatingTimeResolution() {
    return cumulativeOperatingTimeResolution;
  }

  public int getNumberOfBatteries() {
    return numberOfBatteries;
  }

  public int getBatteryIdentifier() {
    return batteryIdentifier;
  }
  
  public boolean isUploaded() {
    return uploaded;
  }
}
