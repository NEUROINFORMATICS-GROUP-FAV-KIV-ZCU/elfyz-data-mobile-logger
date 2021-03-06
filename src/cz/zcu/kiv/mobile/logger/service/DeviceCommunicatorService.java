package cz.zcu.kiv.mobile.logger.service;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.service.communicators.heart_rate.HeartRateCommunicator;
import cz.zcu.kiv.mobile.logger.service.communicators.heart_rate.HeartRateCommunicator.HeartRateListener;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public class DeviceCommunicatorService extends Service {
//  private static final String TAG = DeviceCommunicatorService.class.getSimpleName();
  
  private static final int NOTIFICATION_ID = 123;
  
  private DeviceCommunicatorService service = DeviceCommunicatorService.this;
  private Binder binder = new DeviceCommunicatorBinder();
  
  private boolean running = false;
  private boolean starting = false;
  
  protected HeartRateCommunicator heartRateCommunicator = new HeartRateCommunicator(service);

  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    running = true;
    
    Notification notification = createNotification();
    startForeground(NOTIFICATION_ID, notification);
    
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    CloseUtils.close(
        heartRateCommunicator
      );
    stopForeground(true);
    running = false;
  }
  
  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }
  
  
  protected void startServiceInfinitely(Activity caller) {
    if(running || starting) return;
    
    starting = true;
    startService(new Intent(caller, DeviceCommunicatorService.class));
  }
  
  private Notification createNotification() {
    Intent managementIntent = new Intent(service, ServiceManagementActivity.class);
    PendingIntent pendingManagementIntent = PendingIntent.getActivity(service, 0, managementIntent, 0);
    
    Notification notice = new NotificationCompat.Builder(service)
        .setContentTitle("Mobile Logger") //TODO string?
        .setOngoing(true)
        .setTicker(getString(R.string.service_now_running))
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentIntent(pendingManagementIntent)
        .build();
    
    return notice;
  }
  
  
  public class DeviceCommunicatorBinder extends Binder {
    
    public synchronized void startHeartRate(Activity caller, HeartRateListener listener) {
      startServiceInfinitely(caller);
      heartRateCommunicator.startListening(caller, listener);
    }
    
    public synchronized void stopHeartRate(HeartRateListener listener) {
      heartRateCommunicator.stopListening(listener);
    }
  }
}
