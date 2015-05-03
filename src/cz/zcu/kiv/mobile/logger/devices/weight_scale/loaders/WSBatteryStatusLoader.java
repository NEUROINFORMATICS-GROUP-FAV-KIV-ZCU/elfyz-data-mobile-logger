package cz.zcu.kiv.mobile.logger.devices.weight_scale.loaders;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleBatteryStatusTable.WSBatteryStatusObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.loaders.AMeasurementListLoader;


public class WSBatteryStatusLoader extends AMeasurementListLoader {
  protected WSBatteryStatusObserver observer;
  

  public WSBatteryStatusLoader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected void ensureObserverUnregistered() {
    if(observer != null) {
      db.getWeightScaleBatteryStatusTable().removeObserver(observer);
      observer = null;
    }
  }

  @Override
  protected void ensureObserverRegistered() {
    if(observer == null) {
      observer = new WSBatteryStatusObserver() {
        @Override
        public void onWSBatteryStatusDataUpdated(long[] ids) {
          onContentChanged();
        }
        @Override
        public void onWSBatteryStatusDataAdded(long id) {
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
      db.getWeightScaleBatteryStatusTable().addObserver(observer);
    }
  }

  @Override
  protected Cursor getMeasurements() throws DatabaseException {
    return db.getWeightScaleBatteryStatusTable().getMeasurements(profileID);
  }
}
