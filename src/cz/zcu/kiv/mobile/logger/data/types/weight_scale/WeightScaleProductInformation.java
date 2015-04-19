package cz.zcu.kiv.mobile.logger.data.types.weight_scale;

import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;


public class WeightScaleProductInformation {
  private long estTimestamp;
  private EnumSet<EventFlag> eventFlags;
  private int mainSoftwareRevision;
  private int supplementalSoftwareRevision;
  private long serialNumber;
  private boolean uploaded;
  
  
  public WeightScaleProductInformation(long estTimestamp, EnumSet<EventFlag> eventFlags, int mainSoftwareRevision,
      int supplementalSoftwareRevision, long serialNumber, boolean uploaded) {
    this.estTimestamp = estTimestamp;
    this.eventFlags = eventFlags;
    this.mainSoftwareRevision = mainSoftwareRevision;
    this.supplementalSoftwareRevision = supplementalSoftwareRevision;
    this.serialNumber = serialNumber;
    this.uploaded = uploaded;
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
  
  public boolean isUploaded() {
    return uploaded;
  }
}
