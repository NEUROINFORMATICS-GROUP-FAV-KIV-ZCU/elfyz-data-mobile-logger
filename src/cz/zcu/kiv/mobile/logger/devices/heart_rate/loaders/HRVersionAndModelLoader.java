package cz.zcu.kiv.mobile.logger.devices.heart_rate.loaders;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.AMeasurementListLoader;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateVersionAndModelTable.HRVersionAndModelObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;


public class HRVersionAndModelLoader extends AMeasurementListLoader {
  protected HRVersionAndModelObserver observer;
  

  public HRVersionAndModelLoader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected void ensureObserverUnregistered() {
    if(observer != null) {
      db.getHeartRateVersionAndModelTable().removeObserver(observer);
      observer = null;
    }
  }

  @Override
  protected void ensureObserverRegistered() {
    if(observer == null) {
      observer = new HRVersionAndModelObserver() {
        @Override
        public void onHRVersionAndModelDataUpdated(long[] ids) {
          onContentChanged();
        }
        @Override
        public void onHRVersionAndModelDataAdded(long id) {
          onContentChanged();
        }
        @Override
        public void onClear() {
          onContentChanged();
        }
        @Override
        public void onRecordsDeleted(long... ids) {
          onContentChanged();
        }
      };
      db.getHeartRateVersionAndModelTable().addObserver(observer);
    }
  }

  @Override
  protected Cursor getMeasurements() throws DatabaseException {
    return db.getHeartRateVersionAndModelTable().getMeasurements(profileID);
  }
}
