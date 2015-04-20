package cz.zcu.kiv.mobile.logger.devices.heart_rate.loaders;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.AMeasurementListLoader;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateCumulativeOperatingTimeTable.HRCumulativeOperatingTimeObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;


public class HRCumulativeOperatingTimeLoader extends AMeasurementListLoader {
  protected HRCumulativeOperatingTimeObserver observer;
  

  public HRCumulativeOperatingTimeLoader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected void ensureObserverUnregistered() {
    if(observer != null) {
      db.getHeartRateCumulativeOperatingTimeTable().removeObserver(observer);
      observer = null;
    }
  }

  @Override
  protected void ensureObserverRegistered() {
    if(observer == null) {
      observer = new HRCumulativeOperatingTimeObserver() {
        @Override
        public void onHRCumulativeOperatingTimeDataUpdated(long[] ids) {
          onContentChanged();
        }
        @Override
        public void onHRCumulativeOperatingTimeDataAdded(long id) {
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
      db.getHeartRateCumulativeOperatingTimeTable().addObserver(observer);
    }
  }

  @Override
  protected Cursor getMeasurements() throws DatabaseException {
    return db.getHeartRateCumulativeOperatingTimeTable().getMeasurements(profileID);
  }
}
