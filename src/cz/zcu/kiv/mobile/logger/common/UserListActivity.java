package cz.zcu.kiv.mobile.logger.common;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public class UserListActivity extends ListActivity {
  private static final String TAG = UserListActivity.class.getSimpleName();

  protected Profile userProfile;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    userProfile = Application.getInstance().getUserProfileOrLogIn();
    
    if(userProfile == null){
      AndroidUtils.toast(this, R.string.alert_must_be_logged_in);
      Log.e(TAG, "User must be logged in.");
      finish();
    }
  }
}
