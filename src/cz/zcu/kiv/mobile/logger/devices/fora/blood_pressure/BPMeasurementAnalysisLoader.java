package cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.database.BloodPressureMeasurementTable.BPDataObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.loaders.AAnalysisLoader;


public class BPMeasurementAnalysisLoader extends AAnalysisLoader {
  protected BPDataObserver observer;
  

  public BPMeasurementAnalysisLoader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected Cursor loadData() throws DatabaseException {
    return db.getBloodPressureMeasurementTable().getMeasurements(profileID, true);
  }

  @Override
  protected Cursor loadData(Date dateFrom, Date dateTo) throws DatabaseException {
    return db.getBloodPressureMeasurementTable().getMeasurements(profileID, true, dateFrom, dateTo);
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
        @Override
        public void onRecordsDeleted(long... ids) {
          onContentChanged();
        }
      };
      db.getBloodPressureMeasurementTable().addObserver(observer);
    }
  }
}
