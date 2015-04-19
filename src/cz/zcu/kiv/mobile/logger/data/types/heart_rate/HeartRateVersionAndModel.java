package cz.zcu.kiv.mobile.logger.data.types.heart_rate;

import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;


public class HeartRateVersionAndModel {
  private long estTimestamp;
  private EnumSet<EventFlag> eventFlags;
  private int hardwareVersion;
  private int softwareVersion;
  private int modelNumber;
  private boolean uploaded;
  
  
  public HeartRateVersionAndModel(long estTimestamp, EnumSet<EventFlag> eventFlags, int hardwareVersion,
          int softwareVersion, int modelNumber, boolean uploaded) {
    this.estTimestamp = estTimestamp;
    this.eventFlags = eventFlags;
    this.hardwareVersion = hardwareVersion;
    this.softwareVersion = softwareVersion;
    this.modelNumber = modelNumber;
    this.uploaded = uploaded;
  }


  public long getEstTimestamp() {
    return estTimestamp;
  }

  public EnumSet<EventFlag> getEventFlags() {
    return eventFlags;
  }

  public int getHardwareVersion() {
    return hardwareVersion;
  }

  public int getSoftwareVersion() {
    return softwareVersion;
  }

  public int getModelNumber() {
    return modelNumber;
  }
  
  public boolean isUploaded() {
    return uploaded;
  }
}
