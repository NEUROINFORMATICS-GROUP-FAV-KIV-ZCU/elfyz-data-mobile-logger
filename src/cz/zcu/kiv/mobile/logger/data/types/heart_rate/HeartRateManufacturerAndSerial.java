package cz.zcu.kiv.mobile.logger.data.types.heart_rate;

import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;


public class HeartRateManufacturerAndSerial {
  private long estTimestamp;
  private EnumSet<EventFlag> eventFlags;
  private int manufacturerID;
  private int serialNumber;
  private boolean uploaded;
  
  
  public HeartRateManufacturerAndSerial(long estTimestamp, EnumSet<EventFlag> eventFlags, int manufacturerID, int serialNumber, boolean uploaded) {
    this.estTimestamp = estTimestamp;
    this.eventFlags = eventFlags;
    this.manufacturerID = manufacturerID;
    this.serialNumber = serialNumber;
    this.uploaded = uploaded;
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
  
  public boolean isUploaded() {
    return uploaded;
  }
}
