package cz.zcu.kiv.mobile.logger.devices.weight_scale.loaders;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.AMeasurementListLoader;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleManufacturerIdentificationTable.WSManufacturerIdentificationObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;


public class WSManufacturerIdentificationLoader extends AMeasurementListLoader {
  protected WSManufacturerIdentificationObserver observer;
  

  public WSManufacturerIdentificationLoader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected void ensureObserverUnregistered() {
    if(observer != null) {
      db.getWeightScaleManufacturerIdentificationTable().removeObserver(observer);
      observer = null;
    }
  }

  @Override
  protected void ensureObserverRegistered() {
    if(observer == null) {
      observer = new WSManufacturerIdentificationObserver() {
        @Override
        public void onWSManufacturerIdentificationDataUpdated(long[] ids) {
          onContentChanged();
        }
        @Override
        public void onWSManufacturerIdentificationDataAdded(long id) {
          onContentChanged();
        }
      };
      db.getWeightScaleManufacturerIdentificationTable().addObserver(observer);
    }
  }

  @Override
  protected Cursor getMeasurements() throws DatabaseException {
    return db.getWeightScaleManufacturerIdentificationTable().getMeasurements(profileID);
  }
}
