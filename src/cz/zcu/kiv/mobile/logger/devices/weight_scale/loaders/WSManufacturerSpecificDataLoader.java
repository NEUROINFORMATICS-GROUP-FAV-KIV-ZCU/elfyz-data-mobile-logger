package cz.zcu.kiv.mobile.logger.devices.weight_scale.loaders;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleManufacturerSpecificDataTable.WSManufacturerSpecificDataObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.loaders.AMeasurementListLoader;


public class WSManufacturerSpecificDataLoader extends AMeasurementListLoader {
  protected WSManufacturerSpecificDataObserver observer;
  

  public WSManufacturerSpecificDataLoader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected void ensureObserverUnregistered() {
    if(observer != null) {
      db.getWeightScaleManufacturerSpecificDataTable().removeObserver(observer);
      observer = null;
    }
  }

  @Override
  protected void ensureObserverRegistered() {
    if(observer == null) {
      observer = new WSManufacturerSpecificDataObserver() {
        @Override
        public void onWSManufacturerSpecificDataUpdated(long[] ids) {
          onContentChanged();
        }
        @Override
        public void onWSManufacturerSpecificDataAdded(long id) {
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
      db.getWeightScaleManufacturerSpecificDataTable().addObserver(observer);
    }
  }

  @Override
  protected Cursor getMeasurements() throws DatabaseException {
    return db.getWeightScaleManufacturerSpecificDataTable().getMeasurements(profileID);
  }
}
