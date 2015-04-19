package cz.zcu.kiv.mobile.logger.sync;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * A bound Service that instantiates the authenticator
 * when started.
 */
public class AuthenticatorService extends Service {
  public static final String AUTHORITY = "cz.zcu.kiv.mobile.logger.provider";
  public static final String ACCOUNT_TYPE = "cz.zcu.kiv.mobile.logger";


  // Instance field that stores the authenticator object
  private StubAuthenticator mAuthenticator;


  @Override
  public void onCreate() {
    // Create a new authenticator object
    mAuthenticator = new StubAuthenticator(this);
  }

  /*
   * When the system binds to this Service to make the RPC call
   * return the authenticator's IBinder.
   */
  @Override
  public IBinder onBind(Intent intent) {
    return mAuthenticator.getIBinder();
  }
}