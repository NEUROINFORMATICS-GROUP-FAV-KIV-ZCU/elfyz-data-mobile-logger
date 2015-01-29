package cz.zcu.kiv.mobile.logger;

import android.content.Intent;
import cz.zcu.kiv.mobile.logger.data.database.Database;
import cz.zcu.kiv.mobile.logger.data.types.Profile;


public class Application extends android.app.Application {
  private static Application instance;
  private Database db;
  private Profile userProfile;
  
  
  public Application() {
    instance = this;
    db = new Database(this);
  }

  
  public static Application getInstance() {
    return instance;
  }
  
  public Database getDatabase() {
    return db;
  }
  
  public Profile getUserProfile() {
    return userProfile;
  }
  
  public Profile getUserProfileOrLogIn() {
    if(userProfile == null) {
      Intent login = new Intent(this, MainActivity.class);
      login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);  //TODO ukončit služby... to samé při odhlašování (z každé aktivity? zde mít odhlašovací logiku?)
      startActivity(login);
    }
    return userProfile;
  }
  
  public void setUserProfile(Profile userProfile) {
    this.userProfile = userProfile;
  }

  
  public static String getStringResource(int textId) {
    return instance.getString(textId);
  }
}
