package cz.zcu.kiv.mobile.logger.devices.heart_rate.loaders;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.AMeasurementListLoader;
import cz.zcu.kiv.mobile.logger.data.database.HeartRatePage4Table.HRPage4Observer;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;


public class HRPage4Loader extends AMeasurementListLoader {
  protected HRPage4Observer observer;
  

  public HRPage4Loader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected void ensureObserverUnregistered() {
    if(observer != null) {
      db.getHeartRatePage4Table().removeObserver(observer);
      observer = null;
    }
  }

  @Override
  protected void ensureObserverRegistered() {
    if(observer == null) {
      observer = new HRPage4Observer() {
        @Override
        public void onHRPage4DataUpdated(long[] ids) {
          onContentChanged();
        }
        @Override
        public void onHRPage4DataAdded(long id) {
          onContentChanged();
        }
        @Override
        public void onClear() {
          onContentChanged();
        }
      };
      db.getHeartRatePage4Table().addObserver(observer);
    }
  }

  @Override
  protected Cursor getMeasurements() throws DatabaseException {
    return db.getHeartRatePage4Table().getMeasurements(profileID);
  }
}
