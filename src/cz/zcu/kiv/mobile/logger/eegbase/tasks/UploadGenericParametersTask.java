package cz.zcu.kiv.mobile.logger.eegbase.tasks;

import android.os.AsyncTask;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.eegbase.EegbaseRest;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.AddExperimentDataResult;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.ExperimentParametersData;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.CommunicationException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.WrongCredentialsException;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.IExperimentParametersUploadHelper;


public class UploadGenericParametersTask extends AsyncTask<Void, Void, AsyncTaskResult<AddExperimentDataResult>> {
  private ExperimentParametersData parameters;
  private IExperimentParametersUploadHelper uploadHelper;
  
  
  public UploadGenericParametersTask(ExperimentParametersData parameters) {
    this.parameters = parameters;
  }
  
  public UploadGenericParametersTask(IExperimentParametersUploadHelper uploadHelper) {
    this.uploadHelper = uploadHelper;
  }

  
  @Override
  protected AsyncTaskResult<AddExperimentDataResult> doInBackground(Void... params) {
    AsyncTaskResult<AddExperimentDataResult> result = new AsyncTaskResult<AddExperimentDataResult>();
    
    try {
      //use parameters
      if(parameters != null) {
        result.setResult(EegbaseRest.uploadGenericParameters(parameters));
      }
      //or upload helper
      else {
        parameters = uploadHelper.buildParameters();
        
        result.setResult(EegbaseRest.uploadGenericParameters(parameters));
        
        uploadHelper.markUploaded();
      }
    }
    catch (WrongCredentialsException e) {
      result.setError(e);
    }
    catch (CommunicationException e) {
      result.setError(e);
    }
    catch (UploadHelperException e) {
      result.setError(e);
    }
    return result;
  }
}