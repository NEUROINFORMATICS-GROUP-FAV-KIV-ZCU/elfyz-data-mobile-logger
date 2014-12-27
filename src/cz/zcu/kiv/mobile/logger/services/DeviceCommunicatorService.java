package cz.zcu.kiv.mobile.logger.services;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import cz.zcu.kiv.mobile.logger.services.HeartRateCommunicator.HeartRateListener;


public class DeviceCommunicatorService extends Service {
  private static final String TAG = DeviceCommunicatorService.class.getSimpleName();
  
  private static final int NOTIFICATION_ID = 123;
  
  public static final String HEART_EVENT = "heart.event";
  public static final String DATA = "data";

  private DeviceCommunicatorService service = DeviceCommunicatorService.this;
  private Binder binder = new DeviceCommunicatorBinder();
  
  private boolean running = false;
  
  protected HeartRateCommunicator heartRateCommunicator = new HeartRateCommunicator(service);

  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    running = true;
    
    Notification notification = createNotification();
    
//    startForeground(NOTIFICATION_ID, notification); //TODO
    
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    heartRateCommunicator.close();
    running = false;
  }
  
  @Override
  public IBinder onBind(Intent arg0) {
    return binder;
  }
  
  
  protected void startServiceInfinitely(Activity caller) {
    if(running) return;
      
    startService(new Intent(caller, DeviceCommunicatorService.class));
  }
  
  private Notification createNotification() {
    Notification notice = new NotificationCompat.Builder(service)
        .setContentTitle("Mobile Logger")
        .setOngoing(true)
        .setTicker("Logger service is now running...")
//        .setContentIntent(intent)
        .build();
    
    return notice;
  }
  
  
  
  public class DeviceCommunicatorBinder extends Binder {
    
    public synchronized void startHeartRate(Activity caller, HeartRateListener listener) {
      startServiceInfinitely(caller);
      heartRateCommunicator.startListening(caller, listener);
    }
    
    public synchronized void stopHeartRate(HeartRateListener listener) { //TODO test and finish service
      heartRateCommunicator.stopListening(listener);
    }
  }
}
