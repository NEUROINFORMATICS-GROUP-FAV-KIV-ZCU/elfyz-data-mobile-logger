package cz.zcu.kiv.mobile.logger.devices.weight_scale;

import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;


public class WeightScaleProductInformation {
  private long estTimestamp;
  private EnumSet<EventFlag> eventFlags;
  private int mainSoftwareRevision;
  private int supplementalSoftwareRevision;
  private long serialNumber;
  
  
  public WeightScaleProductInformation(long estTimestamp, EnumSet<EventFlag> eventFlags, int mainSoftwareRevision,
      int supplementalSoftwareRevision, long serialNumber) {
    this.estTimestamp = estTimestamp;
    this.eventFlags = eventFlags;
    this.mainSoftwareRevision = mainSoftwareRevision;
    this.supplementalSoftwareRevision = supplementalSoftwareRevision;
    this.serialNumber = serialNumber;
  }
  

  public long getEstTimestamp() {
    return estTimestamp;
  }

  public EnumSet<EventFlag> getEventFlags() {
    return eventFlags;
  }

  public int getMainSoftwareRevision() {
    return mainSoftwareRevision;
  }

  public int getSupplementalSoftwareRevision() {
    return supplementalSoftwareRevision;
  }

  public long getSerialNumber() {
    return serialNumber;
  }
}
