package cz.zcu.kiv.mobile.logger.devices.heart_rate.loaders;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.AMeasurementListLoader;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateManufacturerAndSerialTable.HRManufacturerAndSerialObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;


public class HRManufacturerAndSerialLoader extends AMeasurementListLoader {
  protected HRManufacturerAndSerialObserver observer;
  

  public HRManufacturerAndSerialLoader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected void ensureObserverUnregistered() {
    if(observer != null) {
      db.getHeartRateManufacturerAndSerialTable().removeObserver(observer);
      observer = null;
    }
  }

  @Override
  protected void ensureObserverRegistered() {
    if(observer == null) {
      observer = new HRManufacturerAndSerialObserver() {
        @Override
        public void onHRManufacturerAndSerialDataUpdated(long[] ids) {
          onContentChanged();
        }
        @Override
        public void onHRManufacturerAndSerialDataAdded(long id) {
          onContentChanged();
        }
      };
      db.getHeartRateManufacturerAndSerialTable().addObserver(observer);
    }
  }

  @Override
  protected Cursor getMeasurements() throws DatabaseException {
    return db.getHeartRateManufacturerAndSerialTable().getMeasurements(profileID);
  }
}
