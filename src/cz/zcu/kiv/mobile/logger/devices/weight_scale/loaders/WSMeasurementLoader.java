package cz.zcu.kiv.mobile.logger.devices.weight_scale.loaders;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.AMeasurementListLoader;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleMeasurementTable.WSDataObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;


public class WSMeasurementLoader extends AMeasurementListLoader {
  protected WSDataObserver observer;
  

  public WSMeasurementLoader(Context context, long profileID) {
    super(context, profileID);
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
      };
      db.getWeightScaleMeasurementTable().addObserver(observer);
    }
  }

  @Override
  protected Cursor getMeasurements() throws DatabaseException {
    return db.getWeightScaleMeasurementTable().getMeasurements(profileID);
  }
}
