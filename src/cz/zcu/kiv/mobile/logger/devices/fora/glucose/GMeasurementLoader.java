package cz.zcu.kiv.mobile.logger.devices.fora.glucose;

import java.util.List;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.data.database.GlucoseMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.GlucoseMeasurementTable.GDataObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtil;


public class GMeasurementLoader extends AsyncTaskLoader<AsyncTaskResult<Cursor>> {
  protected GlucoseMeasurementTable dbGM;
  protected long profileID;
  
  protected AsyncTaskResult<Cursor> data;
  protected GDataObserver observer;
  
  
  public GMeasurementLoader(Context context, long profileID) {
    super(context);
    this.profileID = profileID;
    dbGM = Application.getInstance().getDatabase().getGlucoseMeasurementTable();
  }

  
  @Override
  public AsyncTaskResult<Cursor> loadInBackground() {
    AsyncTaskResult<Cursor> result = new AsyncTaskResult<Cursor>();
    
    try {
      Cursor cursor = dbGM.getMeasurements(profileID);
      result.setResult(cursor);
    }
    catch (DatabaseException e) {
      result.setError(e);
    }
    
    return result;
  }
  
  @Override
  public void deliverResult(AsyncTaskResult<Cursor> data) {
    if(isReset()) {
      releaseResources(data);
      return;
    }
    
    AsyncTaskResult<Cursor> oldData = this.data;
    
    if(isStarted()) {
      super.deliverResult(data);
    }
    
    if(oldData != null && oldData != data) {
      releaseResources(oldData);
    }
    
    this.data = data;
  }
  
  @Override
  protected void onStartLoading() {
    if(data != null) {
      deliverResult(data);
    }
    
    if(observer == null) {
      observer = new GDataObserver() {
        @Override
        public void onGlucoseMeasurementAdded(long id) {
          onContentChanged();
        }
        @Override
        public void onGlucoseMeasurementAdded(List<Long> ids) {
          onContentChanged();
        }
        @Override
        public void onGlucoseMeasurementsUpdated(long[] ids) {
          onContentChanged();
        }
      };
      dbGM.addObserver(observer);
    }
    
    if(takeContentChanged() || data == null) {
      forceLoad();
    }
  }

  @Override
  protected void onStopLoading() {
    cancelLoad();
  }
  
  @Override
  protected void onReset() {
    onStopLoading();
    
    if(data != null) {
      releaseResources(data);
    }
    
    if(observer != null) {
      dbGM.removeObserver(observer);
      observer = null;
    }
  }
  
  @Override
  public void onCanceled(AsyncTaskResult<Cursor> data) {
    super.onCanceled(data);
    releaseResources(data);
  }

  private void releaseResources(AsyncTaskResult<Cursor> data) {
    if(data != null) {
      CloseUtil.close(data.getResult());
    }
    this.data = null;
  }
}
