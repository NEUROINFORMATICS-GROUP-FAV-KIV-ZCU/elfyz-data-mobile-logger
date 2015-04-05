package cz.zcu.kiv.mobile.logger.data.types;


public class NamedClass {
  public String name;
  public Class<?> clazz;
  
  public NamedClass(String name, Class<?> clazz) {
    this.name = name;
    this.clazz = clazz;
  }
}
