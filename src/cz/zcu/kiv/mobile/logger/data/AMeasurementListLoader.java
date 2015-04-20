package cz.zcu.kiv.mobile.logger.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.Database;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public abstract class AMeasurementListLoader extends AsyncTaskLoader<AsyncTaskResult<Cursor>> {
  protected Database db;
  protected long profileID;
  
  protected AsyncTaskResult<Cursor> data;
  

  public AMeasurementListLoader(Context context, long profileID) {
    super(context);
    this.profileID = profileID;
    db = Application.getInstance().getDatabase();
  }

  
  @Override
  public AsyncTaskResult<Cursor> loadInBackground() {
    AsyncTaskResult<Cursor> result = new AsyncTaskResult<Cursor>();
    
    try {
      Cursor cursor = getMeasurements();
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
    
    ensureObserverRegistered();
    
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
    
    ensureObserverUnregistered();
  }


  @Override
  public void onCanceled(AsyncTaskResult<Cursor> data) {
    super.onCanceled(data);
    releaseResources(data);
  }

  private void releaseResources(AsyncTaskResult<Cursor> data) {
    if(data != null) {
      CloseUtils.close(data.getResult());
    }
    this.data = null;
  }
  
  
  
  protected abstract void ensureObserverUnregistered();
  protected abstract void ensureObserverRegistered();
  protected abstract Cursor getMeasurements() throws DatabaseException;
}
