package cz.zcu.kiv.mobile.logger.utils;

import java.io.Closeable;

import android.util.Log;


public class CloseUtil {
  private static final String TAG = CloseUtil.class.getSimpleName();
  

  private CloseUtil() {}

  
  public static void close(Closeable... closeables) {
    for (Closeable closeable : closeables) {
      if(closeable != null){
        try{
          closeable.close();
        }
        catch(Exception e){
          Log.w(TAG, "Failed to close closeable: " + closeable.getClass().getName() + " - " + closeable.toString(), e);
        }
      }
    }
  }
}
