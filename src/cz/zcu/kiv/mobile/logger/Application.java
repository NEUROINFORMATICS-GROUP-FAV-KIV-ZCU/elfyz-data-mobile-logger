package cz.zcu.kiv.mobile.logger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import cz.zcu.kiv.mobile.logger.data.database.Database;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.service.DeviceCommunicatorService;


public class Application extends android.app.Application {
  private static Application instance;
  private Database db;
  private Profile userProfile;
  
  
  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
    db = new Database(this);
  }

  
  public static Application getInstance() {
    return instance;
  }
  
  public Database getDatabase() {
    return db;
  }
  
  public Profile getUserProfileOrLogIn() {
    if(userProfile == null) {
      Intent login = new Intent(this, MainActivity.class);
      login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
      AndroidUtils.toast(instance, R.string.fail_bt_not_selected_need_log_in);
      startActivity(login);
    }
    return userProfile;
  }
  
  public void setUserProfile(Profile userProfile) {
    stopService(new Intent(this, DeviceCommunicatorService.class));
    this.userProfile = userProfile;
  }

  
  public static String getStringResource(int textId) {
    return instance.getString(textId);
  }
  
  public static SharedPreferences getPreferences() {
    return PreferenceManager.getDefaultSharedPreferences(instance);
  }
}
