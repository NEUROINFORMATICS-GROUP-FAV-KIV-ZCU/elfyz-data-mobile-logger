package cz.zcu.kiv.mobile.logger.devices.fora.glucose;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.AMeasurementListLoader;
import cz.zcu.kiv.mobile.logger.data.database.GlucoseMeasurementTable.GDataObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;


public class GMeasurementLoader extends AMeasurementListLoader {
  protected GDataObserver observer;
  
  
  public GMeasurementLoader(Context context, long profileID) {
    super(context, profileID);
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

  @Override
  protected Cursor getMeasurements() throws DatabaseException {
    return db.getGlucoseMeasurementTable().getMeasurements(profileID);
  }
}
