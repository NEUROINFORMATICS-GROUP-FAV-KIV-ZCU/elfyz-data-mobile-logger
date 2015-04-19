package cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.AMeasurementListLoader;
import cz.zcu.kiv.mobile.logger.data.database.BloodPressureMeasurementTable.BPDataObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;


public class BPMeasurementLoader extends AMeasurementListLoader {
  protected BPDataObserver observer;
  

  public BPMeasurementLoader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected void ensureObserverUnregistered() {
    if(observer != null) {
      db.getBloodPressureMeasurementTable().removeObserver(observer);
      observer = null;
    }
  }

  @Override
  protected void ensureObserverRegistered() {
    if(observer == null) {
      observer = new BPDataObserver() {
        @Override
        public void onClear() {
          onContentChanged();
        }
        @Override
        public void onBPMeasurementsUpdated(long[] ids) {
          onContentChanged();
        }
        @Override
        public void onBPMeasurementBatchAdded(List<Long> ids) {
          onContentChanged();
        }
        @Override
        public void onBPMeasurementAdded(long id) {
          onContentChanged();
        }
      };
      db.getBloodPressureMeasurementTable().addObserver(observer);
    }
  }

  @Override
  protected Cursor getMeasurements() throws DatabaseException {
    return db.getBloodPressureMeasurementTable().getMeasurements(profileID);
  }
}
