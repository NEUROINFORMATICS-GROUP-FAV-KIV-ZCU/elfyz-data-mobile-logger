package cz.zcu.kiv.mobile.logger.utils;

import cz.zcu.kiv.mobile.logger.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkUtils {

  private NetworkUtils() { }

  
  public static boolean isAvailable(boolean respectSettings) {
    ConnectivityManager cm = (ConnectivityManager) Application.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    if (wifiNetwork != null && wifiNetwork.isConnected()) {
      return true;
    }

    NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    if (mobileNetwork != null && mobileNetwork.isConnected()) {
      if(respectSettings && Application.getPreferences().getBoolean("connection_allow_mobile", false) == false) {
        return false;
      }
      return true;
    }

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    if (activeNetwork != null && activeNetwork.isConnected()) {
      return true;
    }

    return false;
  }
}
