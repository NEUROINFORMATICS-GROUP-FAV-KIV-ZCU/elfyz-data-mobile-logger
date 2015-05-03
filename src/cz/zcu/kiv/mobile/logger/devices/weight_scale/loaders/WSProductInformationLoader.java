package cz.zcu.kiv.mobile.logger.devices.weight_scale.loaders;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleProductInformationTable.WSProductInformationObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.loaders.AMeasurementListLoader;


public class WSProductInformationLoader extends AMeasurementListLoader {
  protected WSProductInformationObserver observer;
  

  public WSProductInformationLoader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected void ensureObserverUnregistered() {
    if(observer != null) {
      db.getWeightScaleProductInformationTable().removeObserver(observer);
      observer = null;
    }
  }

  @Override
  protected void ensureObserverRegistered() {
    if(observer == null) {
      observer = new WSProductInformationObserver() {
        @Override
        public void onWSProductInformationDataUpdated(long[] ids) {
          onContentChanged();
        }
        @Override
        public void onWSProductInformationDataAdded(long id) {
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
      db.getWeightScaleProductInformationTable().addObserver(observer);
    }
  }

  @Override
  protected Cursor getMeasurements() throws DatabaseException {
    return db.getWeightScaleProductInformationTable().getMeasurements(profileID);
  }
}
