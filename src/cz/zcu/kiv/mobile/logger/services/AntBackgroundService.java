package cz.zcu.kiv.mobile.logger.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;



//TODO localbroadcastem posílat data, activity si je zobrazí a tady nebo v nìjakém broadcastreceiveru ukládat do db


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
