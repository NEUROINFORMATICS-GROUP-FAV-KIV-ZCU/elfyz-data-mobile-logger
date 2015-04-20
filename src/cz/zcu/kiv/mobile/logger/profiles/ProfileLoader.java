package cz.zcu.kiv.mobile.logger.profiles;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.data.database.ProfileTable;
import cz.zcu.kiv.mobile.logger.data.database.ProfileTable.ProfileDataObserver;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public class ProfileLoader extends AsyncTaskLoader<AsyncTaskResult<Cursor>> { //TODO loader inheritance?
  protected ProfileTable dbProfile;
  
  protected AsyncTaskResult<Cursor> data;
  protected ProfileDataObserver observer;
  

  public ProfileLoader(Context context) {
    super(context);
    dbProfile = Application.getInstance().getDatabase().getProfileTable();
  }

  
  @Override
  public AsyncTaskResult<Cursor> loadInBackground() {
    AsyncTaskResult<Cursor> result = new AsyncTaskResult<Cursor>();
    
    try {
      Cursor cursor = dbProfile.getProfileNames();
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
      observer = new ProfileDataObserver() {
        @Override
        public void onProfileDeleted(long profileID) {
          onContentChanged();
        }
        @Override
        public void onProfileAdded(long id) {
          onContentChanged();
        }
        @Override
        public void onProfileUpdated(long profileID) {
          onContentChanged();
        }
      };
      dbProfile.addObserver(observer);
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
      dbProfile.removeObserver(observer);
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
      CloseUtils.close(data.getResult());
    }
    this.data = null;
  }
}
