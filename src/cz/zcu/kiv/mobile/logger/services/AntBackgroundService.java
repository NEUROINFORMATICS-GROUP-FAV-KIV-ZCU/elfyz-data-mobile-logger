package cz.zcu.kiv.mobile.logger.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;



//TODO localbroadcastem pos�lat data, activity si je zobraz� a tady nebo v n�jak�m broadcastreceiveru ukl�dat do db


public class AntBackgroundService extends Service {

  public AntBackgroundService() {
    // TODO Auto-generated constructor stub
  }

  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    // TODO Auto-generated method stub
    return super.onStartCommand(intent, flags, startId);
  }
  
  @Override
  public IBinder onBind(Intent arg0) {
    // TODO Auto-generated method stub
    return null;
  }

}
