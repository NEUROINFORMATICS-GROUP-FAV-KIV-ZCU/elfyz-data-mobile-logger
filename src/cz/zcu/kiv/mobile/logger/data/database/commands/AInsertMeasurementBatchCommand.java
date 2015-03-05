package cz.zcu.kiv.mobile.logger.data.database.commands;

import java.util.List;

import android.os.AsyncTask;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;


public abstract class AInsertMeasurementBatchCommand<T_Measurement> extends AsyncTask<Void, Void, AsyncTaskResult<List<Long>>> {
  protected long userID;
  protected List<T_Measurement> measurements;
  protected boolean ignoreDuplicates;
  protected InsertBatchCommandListener listener;
  
  
  public AInsertMeasurementBatchCommand(long userID, List<T_Measurement> measurements, boolean ignoreDuplicates, InsertBatchCommandListener listener) {
    this.userID = userID;
    this.measurements = measurements;
    this.ignoreDuplicates = ignoreDuplicates;
    this.listener = listener;
  }

  
  @Override
  protected AsyncTaskResult<List<Long>> doInBackground(Void... params) {
    AsyncTaskResult<List<Long>> result = new AsyncTaskResult<List<Long>>();
    
    try {
      result.setResult(
          insertToDatabase(userID, measurements)
        );
    }
    catch (DatabaseException e) {
      result.setError(e);
    }
    
    return result;
  }

  @Override
  protected void onPostExecute(AsyncTaskResult<List<Long>> result) {
    if(listener != null)
      listener.onInsertBatchCommandFinished(this, result);
  }

  
  abstract protected List<Long> insertToDatabase(long userID, List<T_Measurement> measurements) throws DatabaseException;
  
  
  
  public interface InsertBatchCommandListener {
    void onInsertBatchCommandFinished(AInsertMeasurementBatchCommand<?> command, AsyncTaskResult<List<Long>> result);
  }
}
