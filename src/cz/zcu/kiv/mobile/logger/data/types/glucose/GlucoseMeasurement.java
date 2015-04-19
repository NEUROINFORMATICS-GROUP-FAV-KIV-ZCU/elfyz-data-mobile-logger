package cz.zcu.kiv.mobile.logger.data.types.glucose;

import java.util.Calendar;


public class GlucoseMeasurement {
  private Calendar time;
  private int glucose;
  private int temperature;
  private int code;
  private int type;
  private boolean uploaded;

  
  public GlucoseMeasurement() {}
  
  public GlucoseMeasurement(Calendar time, int glucose, int temperature, int code, int type, boolean uploaded) {
    this.time = time;
    this.glucose = glucose;
    this.temperature = temperature;
    this.code = code;
    this.type = type;
    this.uploaded = uploaded;
  }

  public Calendar getTime() {
    return time;
  }
  
  public void setTime(Calendar time) {
    this.time = time;
  }
  
  public int getGlucose() {
    return glucose;
  }
  
  public void setGlucose(int glucose) {
    this.glucose = glucose;
  }
  
  public int getTemperature() {
    return temperature;
  }
  
  public void setTemperature(int temperature) {
    this.temperature = temperature;
  }
  
  public int getCode() {
    return code;
  }
  
  public void setCode(int code) {
    this.code = code;
  }
  
  public int getType() {
    return type;
  }
  
  public void setType(int type) {
    this.type = type;
  }
  public boolean isUploaded() {
    return uploaded;
  }

  public void setUploaded(boolean uploaded) {
    this.uploaded = uploaded;
  }
}
