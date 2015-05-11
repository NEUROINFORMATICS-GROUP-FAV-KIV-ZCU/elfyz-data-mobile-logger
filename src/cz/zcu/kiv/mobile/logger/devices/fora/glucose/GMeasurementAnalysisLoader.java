package cz.zcu.kiv.mobile.logger.devices.fora.glucose;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.database.GlucoseMeasurementTable.GDataObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.loaders.AAnalysisLoader;


public class GMeasurementAnalysisLoader extends AAnalysisLoader {
  protected GDataObserver observer;
  

  public GMeasurementAnalysisLoader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected Cursor loadData() throws DatabaseException {
    return db.getGlucoseMeasurementTable().getMeasurements(profileID, true);
  }

  @Override
  protected Cursor loadData(Date dateFrom, Date dateTo) throws DatabaseException {
    return db.getGlucoseMeasurementTable().getMeasurements(profileID, true, dateFrom, dateTo);
  }

  @Override
  protected void ensureObserverUnregistered() {
    if(observer != null) {
      db.getGlucoseMeasurementTable().removeObserver(observer);
      observer = null;
    }
  }

  @Override
  protected void ensureObserverRegistered() {
    if(observer == null) {
      observer = new GDataObserver() {
        @Override
        public void onClear() {
          onContentChanged();
        }
        @Override
        public void onGlucoseMeasurementsUpdated(long[] ids) {
          onContentChanged();
        }
        @Override
        public void onGlucoseMeasurementAdded(List<Long> ids) {
          onContentChanged();
        }
        @Override
        public void onGlucoseMeasurementAdded(long id) {
          onContentChanged();
        }
        @Override
        public void onRecordsDeleted(long... ids) {
          onContentChanged();
        }
      };
      db.getGlucoseMeasurementTable().addObserver(observer);
    }
  }
}
