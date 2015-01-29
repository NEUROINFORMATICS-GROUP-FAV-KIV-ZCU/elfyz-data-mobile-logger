package cz.zcu.kiv.mobile.logger.devices.heart_rate;

import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;


public class HeartRateManufacturerAndSerial {
  private long estTimestamp;
  private EnumSet<EventFlag> eventFlags;
  private int manufacturerID;
  private int serialNumber;
  
  
  public HeartRateManufacturerAndSerial(long estTimestamp, EnumSet<EventFlag> eventFlags, int manufacturerID, int serialNumber) {
    this.estTimestamp = estTimestamp;
    this.eventFlags = eventFlags;
    this.manufacturerID = manufacturerID;
    this.serialNumber = serialNumber;
  }
  

  public long getEstTimestamp() {
    return estTimestamp;
  }

  public EnumSet<EventFlag> getEventFlags() {
    return eventFlags;
  }

  public int getManufacturerID() {
    return manufacturerID;
  }

  public int getSerialNumber() {
    return serialNumber;
  }
}
