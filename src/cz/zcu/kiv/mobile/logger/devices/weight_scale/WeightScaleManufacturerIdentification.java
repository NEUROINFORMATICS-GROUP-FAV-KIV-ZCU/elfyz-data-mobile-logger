package cz.zcu.kiv.mobile.logger.devices.weight_scale;

import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;


public class WeightScaleManufacturerIdentification {
  private long estTimestamp;
  private EnumSet<EventFlag> eventFlags;
  private int hardwareRevision;
  private int manufacturerID;
  private int modelNumber;
  private boolean uploaded;
  
  
  public WeightScaleManufacturerIdentification(long estTimestamp, EnumSet<EventFlag> eventFlags,
      int hardwareRevision, int manufacturerID, int modelNumber, boolean uploaded) {
    this.estTimestamp = estTimestamp;
    this.eventFlags = eventFlags;
    this.hardwareRevision = hardwareRevision;
    this.manufacturerID = manufacturerID;
    this.modelNumber = modelNumber;
    this.uploaded = uploaded;
  }

  
  public long getEstTimestamp() {
    return estTimestamp;
  }

  public EnumSet<EventFlag> getEventFlags() {
    return eventFlags;
  }

  public int getHardwareRevision() {
    return hardwareRevision;
  }

  public int getManufacturerID() {
    return manufacturerID;
  }

  public int getModelNumber() {
    return modelNumber;
  }
  
  public boolean isUploaded() {
    return uploaded;
  }
}
