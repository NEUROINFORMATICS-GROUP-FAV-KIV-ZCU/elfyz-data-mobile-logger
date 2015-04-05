package cz.zcu.kiv.mobile.logger.eegbase.data.login;


public class UserInfo {
  private String name;
  private String surname;
  private String rights;
  
  
  public String getName() {
    return name;
  }
  
  public String getSurname() {
    return surname;
  }
  
  public String getRights() {
    return rights;
  }
  
  
  @Override
  public String toString() {
    return new StringBuilder()
    .append("UserInfo[name=").append(name)
    .append(", surname=").append(surname)
    .append(", rights=").append(rights)
    .append("]").toString();
  }
}
