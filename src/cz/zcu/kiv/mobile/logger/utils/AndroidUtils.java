package cz.zcu.kiv.mobile.logger.utils;

import android.content.Context;
import android.widget.Toast;


public class AndroidUtils {
  
  public static void toast(Context context, String text) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
  }
  
  public static void toast(Context context, int resId) {
    Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
  }
}
