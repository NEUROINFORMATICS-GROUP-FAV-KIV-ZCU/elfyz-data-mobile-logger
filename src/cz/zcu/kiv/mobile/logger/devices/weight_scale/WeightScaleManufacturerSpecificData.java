package cz.zcu.kiv.mobile.logger.devices.weight_scale;

import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;


public class WeightScaleManufacturerSpecificData {
  private long estTimestamp;
  private EnumSet<EventFlag> eventFlags;
  private byte[] rawDataBytes;
  private boolean uploaded;
  
  
  public WeightScaleManufacturerSpecificData(long estTimestamp, EnumSet<EventFlag> eventFlags, byte[] rawDataBytes, boolean uploaded) {
    this.estTimestamp = estTimestamp;
    this.eventFlags = eventFlags;
    this.rawDataBytes = rawDataBytes;
    this.uploaded = uploaded;
  }
  
  
  public long getEstTimestamp() {
    return estTimestamp;
  }
  
  public EnumSet<EventFlag> getEventFlags() {
    return eventFlags;
  }
  
  public byte[] getRawDataBytes() {
    return rawDataBytes;
  }
  
  public boolean isUploaded() {
    return uploaded;
  }
}
