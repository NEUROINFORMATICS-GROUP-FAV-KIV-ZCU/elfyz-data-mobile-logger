package cz.zcu.kiv.mobile.logger;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import cz.zcu.kiv.mobile.logger.data.database.Database;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.service.DeviceCommunicatorService;
import cz.zcu.kiv.mobile.logger.sync.AuthenticatorService;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public class Application extends android.app.Application {
  private static Application instance;
  private Database db;
  private Profile userProfile;
  
  
  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
    db = new Database(this);

    PreferenceManager.setDefaultValues(instance, R.xml.preferences_connection, false);
    PreferenceManager.setDefaultValues(instance, R.xml.preferences_gen_pars, false);
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
    
    unsync(this.userProfile);
    sync(userProfile);
    
    this.userProfile = userProfile;
  }

  
  public static String getStringResource(int textId) {
    return instance.getString(textId);
  }
  
  public static SharedPreferences getPreferences() {
    return PreferenceManager.getDefaultSharedPreferences(instance);
  }
  
  
  private void unsync(Profile userProfile) {
    if(userProfile != null) {
      Account account = new Account(String.valueOf(userProfile.getId()), AuthenticatorService.ACCOUNT_TYPE);
      ContentResolver.cancelSync(account, AuthenticatorService.AUTHORITY);
    }
  }
  
  private void sync(Profile userProfile) {
    //TODO data syncing setting
    if(userProfile != null && userProfile.getEegbasePassword() != null) {
      Account account = new Account(String.valueOf(userProfile.getId()), AuthenticatorService.ACCOUNT_TYPE);
//      ContentResolver.setSyncAutomatically(account, AuthenticatorService.AUTHORITY, true);
      ContentResolver.addPeriodicSync(account, AuthenticatorService.AUTHORITY, Bundle.EMPTY, 60L);
    }
  }
}
