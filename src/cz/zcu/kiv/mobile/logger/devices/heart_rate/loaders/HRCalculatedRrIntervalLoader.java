package cz.zcu.kiv.mobile.logger.devices.heart_rate.loaders;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.AMeasurementListLoader;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateCalculatedRrIntervalTable.HRCalculatedRrIntervalObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;


public class HRCalculatedRrIntervalLoader extends AMeasurementListLoader {
  protected HRCalculatedRrIntervalObserver observer;
  

  public HRCalculatedRrIntervalLoader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected void ensureObserverUnregistered() {
    if(observer != null) {
      db.getHeartRateCalculatedRrIntervalTable().removeObserver(observer);
      observer = null;
    }
  }

  @Override
  protected void ensureObserverRegistered() {
    if(observer == null) {
      observer = new HRCalculatedRrIntervalObserver() {
        @Override
        public void onHRCalculatedRrIntervalDataUpdated(long[] ids) {
          onContentChanged();
        }
        @Override
        public void onHRCalculatedRrIntervalDataAdded(long id) {
          onContentChanged();
        }
        @Override
        public void onClear() {
          onContentChanged();
        }
      };
      db.getHeartRateCalculatedRrIntervalTable().addObserver(observer);
    }
  }

  @Override
  protected Cursor getMeasurements() throws DatabaseException {
    return db.getHeartRateCalculatedRrIntervalTable().getMeasurements(profileID);
  }
}
