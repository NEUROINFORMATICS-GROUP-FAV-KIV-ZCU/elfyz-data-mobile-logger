package cz.zcu.kiv.mobile.logger.devices.weight_scale.loaders;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleMeasurementTable.WSDataObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.loaders.AAnalysisLoader;


public class WSMeasurementAnalysisLoader extends AAnalysisLoader {
  protected WSDataObserver observer;
  

  public WSMeasurementAnalysisLoader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected Cursor loadData() throws DatabaseException {
    return db.getWeightScaleMeasurementTable().getMeasurements(profileID, true);
  }

  @Override
  protected Cursor loadData(Date dateFrom, Date dateTo) throws DatabaseException {
    return db.getWeightScaleMeasurementTable().getMeasurements(profileID, true, dateFrom, dateTo);
  }

  @Override
  protected void ensureObserverUnregistered() {
    if(observer != null) {
      db.getWeightScaleMeasurementTable().removeObserver(observer);
      observer = null;
    }
  }

  @Override
  protected void ensureObserverRegistered() {
    if(observer == null) {
      observer = new WSDataObserver() {
        @Override
        public void onWSMeasurementsUpdated(long[] ids) {
          onContentChanged();
        }
        @Override
        public void onWSMeasurementAdded(long id) {
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
      db.getWeightScaleMeasurementTable().addObserver(observer);
    }
  }
}
