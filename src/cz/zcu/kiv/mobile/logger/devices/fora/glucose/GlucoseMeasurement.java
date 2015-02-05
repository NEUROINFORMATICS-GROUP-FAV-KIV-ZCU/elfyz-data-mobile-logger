package cz.zcu.kiv.mobile.logger.devices.fora.glucose;

import java.util.Calendar;


public class GlucoseMeasurement {
  private Calendar time;
  private int glucose;
  private int temperature;
  private int code;
  private int type;

  
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
}
