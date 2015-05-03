package cz.zcu.kiv.mobile.logger.data.database.commands;

import android.os.AsyncTask;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.AsyncTaskResult;


public abstract class AInsertMeasurementCommand<T_Measurement> extends AsyncTask<Void, Void, AsyncTaskResult<Long>> {
  protected long userID;
  protected T_Measurement measurement;
  protected InsertCommandListener listener;
  
  
  public AInsertMeasurementCommand(long userID, T_Measurement measurement, InsertCommandListener listener) {
    this.userID = userID;
    this.measurement = measurement;
    this.listener = listener;
  }

  
  @Override
  protected AsyncTaskResult<Long> doInBackground(Void... params) {
    AsyncTaskResult<Long> result = new AsyncTaskResult<Long>();
    
    try {
      result.setResult(
          insertToDatabase(userID, measurement)
        );
    }
    catch (DatabaseException e) {
      result.setError(e);
    }
    
    return result;
  }

  @Override
  protected void onPostExecute(AsyncTaskResult<Long> result) {
    if(listener != null)
      listener.onInsertCommandFinished(this, result);
  }

  
  abstract protected long insertToDatabase(long userID2, T_Measurement measurement2) throws DatabaseException;
  
  
  
  public interface InsertCommandListener {
    void onInsertCommandFinished(AInsertMeasurementCommand<?> command, AsyncTaskResult<Long> result);
  }
}
