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
  private String experimentID;
  private String email;
  private String password;
  
  
  public UploadGenericParametersTask(String email, String password, String experimentID, ExperimentParametersData parameters) {
    this.experimentID = experimentID;
    this.email = email;
    this.password = password;
    this.parameters = parameters;
  }
  
  public UploadGenericParametersTask(String email, String password, String experimentID, IExperimentParametersUploadHelper uploadHelper) {
    this.experimentID = experimentID;
    this.email = email;
    this.password = password;
    this.uploadHelper = uploadHelper;
  }

  
  @Override
  protected AsyncTaskResult<AddExperimentDataResult> doInBackground(Void... params) {
    AsyncTaskResult<AddExperimentDataResult> result = new AsyncTaskResult<AddExperimentDataResult>();
    
    try {
      //use parameters
      if(parameters != null) {
        result.setResult(EegbaseRest.uploadGenericParameters(email, password, experimentID, parameters));
      }
      //or upload helper
      else {
        parameters = uploadHelper.buildParameters();
        
        result.setResult(EegbaseRest.uploadGenericParameters(email, password, experimentID, parameters));
        
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
