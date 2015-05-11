package cz.zcu.kiv.mobile.logger.devices.heart_rate.loaders;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateMeasurementTable.HRDataObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.loaders.AAnalysisLoader;


public class HRMeasurementAnalysisLoader extends AAnalysisLoader {
  protected HRDataObserver observer;
  

  public HRMeasurementAnalysisLoader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected Cursor loadData() throws DatabaseException {
    return db.getHeartRateMeasurementTable().getMeasurements(profileID, true);
  }

  @Override
  protected Cursor loadData(Date dateFrom, Date dateTo) throws DatabaseException {
    return db.getHeartRateMeasurementTable().getMeasurements(profileID, true, dateFrom, dateTo);
  }

  @Override
  protected void ensureObserverUnregistered() {
    if(observer != null) {
      db.getHeartRateMeasurementTable().removeObserver(observer);
      observer = null;
    }
  }

  @Override
  protected void ensureObserverRegistered() {
    if(observer == null) {
      observer = new HRDataObserver() {
        @Override
        public void onHRMeasurementsUpdated(long[] ids) {
          onContentChanged();
        }
        @Override
        public void onHRMeasurementAdded(long id) {
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
      db.getHeartRateMeasurementTable().addObserver(observer);
    }
  }
}
