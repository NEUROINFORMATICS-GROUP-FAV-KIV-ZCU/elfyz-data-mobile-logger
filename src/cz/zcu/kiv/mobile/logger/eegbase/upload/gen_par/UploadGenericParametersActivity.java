package cz.zcu.kiv.mobile.logger.eegbase.upload.gen_par;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.types.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.eegbase.EegbaseRest;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.CommunicationException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.WrongCredentialsException;
import cz.zcu.kiv.mobile.logger.eegbase.types.add_experiment_parameters.AddExperimentDataResult;
import cz.zcu.kiv.mobile.logger.eegbase.types.add_experiment_parameters.ExperimentParametersData;
import cz.zcu.kiv.mobile.logger.eegbase.types.add_experiment_parameters.GenericParameterData;
import cz.zcu.kiv.mobile.logger.eegbase.types.get_experiment_list.Experiment;
import cz.zcu.kiv.mobile.logger.eegbase.upload.helpers.IExperimentParametersUploadHelper;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public class UploadGenericParametersActivity extends Activity {
  private static final String TAG = UploadGenericParametersActivity.class.getSimpleName();
  
  public static final String PARAM_GENERIC_PARAMETERS = "generic.parameters";
  public static final String PARAM_GENERIC_PARAMETERS_UPLOAD_HELPER = "generis.parameters.upload.helper";
  
  private static final String STATE_EXPERIMENT = "state.experiment";
  private static final int REQUEST_SELECT_EXPERIMENT = 11;

  private Profile userProfile;
  
  private ExperimentParametersData data;
  private IExperimentParametersUploadHelper uploadHelper;
  
  private UploadGenericParametersTask taskUpload = null;
  private Experiment experiment = null;
  
  private TextView tvDataInfo;
  private TextView tvExperimentID;
  private TextView tvScenarioName;
  private TextView tvResearchGroupName;
  private TextView tvTimeFrom;
  private TextView tvTimeTo;
  private TextView tvSubjectName;
  private TextView tvSubjectSurname;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_upload_generic_parameters);
    
    userProfile = Application.getInstance().getUserProfileOrLogIn();
    
    if(userProfile == null) {
      AndroidUtils.toast(this, R.string.alert_must_be_logged_in);
      Log.e(TAG, "User must be logged in.");
      finish();
      return;
    }
    
    tvDataInfo = (TextView) findViewById(R.id.tv_data_info);
    tvExperimentID = (TextView) findViewById(R.id.tv_experiment_id);
    tvScenarioName = (TextView) findViewById(R.id.tv_scenario_name);
    tvResearchGroupName = (TextView) findViewById(R.id.tv_research_group_name);
    tvTimeFrom = (TextView) findViewById(R.id.tv_time_from);
    tvTimeTo = (TextView) findViewById(R.id.tv_time_to);
    tvSubjectName = (TextView) findViewById(R.id.tv_subject_name);
    tvSubjectSurname = (TextView) findViewById(R.id.tv_subject_surname);
    
    //upload parameters
    data = getIntent().getParcelableExtra(PARAM_GENERIC_PARAMETERS);
    if(data == null) {
      //use helper to upload parameters
      uploadHelper = getIntent().getParcelableExtra(PARAM_GENERIC_PARAMETERS_UPLOAD_HELPER);
      //both null
      if(uploadHelper == null) {
        AndroidUtils.toast(this, R.string.fail_no_upload_data_selected);
        finish();
        return;
      }
      else {
        setDataInfo(uploadHelper);
      }
    }
    else {
      setDataInfo(data);
    }
  }
  
  @Override
  protected void onDestroy() {
    if(taskUpload != null) {
      taskUpload.cancel(true);
    }
    super.onDestroy();
  }
  
  private void setDataInfo(ExperimentParametersData data) {
    List<GenericParameterData> parameters = data.getGenericParameters().getGenericParameters();
    if(parameters.size() > 1) {
      tvDataInfo.setText(parameters.size() + " " + getString(R.string.parameters_for_upload));
    }
  }

  private void setDataInfo(IExperimentParametersUploadHelper uploadHelper) {
    tvDataInfo.setText(uploadHelper.getDisplayableInfo());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.upload_generic_parameters, menu);
    return true;
  }
  
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if(experiment != null)
      outState.putParcelable(STATE_EXPERIMENT, experiment);
  }
  
  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    experiment = savedInstanceState.getParcelable(STATE_EXPERIMENT);
    setExperimentInfo(experiment);
  }
  
  private void setExperimentInfo(Experiment experiment) {
    if(experiment == null) {
      String value = getString(R.string.value_n_a);
      tvExperimentID.setText(value);
      tvScenarioName.setText(value);
      tvResearchGroupName.setText(value);
      tvTimeFrom.setText(value);
      tvTimeTo.setText(value);
      tvSubjectName.setText(value);
      tvSubjectSurname.setText(value);
    }
    else {
      tvExperimentID.setText(experiment.getExperimentId());
      tvScenarioName.setText(experiment.getScenarioName());
      tvResearchGroupName.setText(experiment.getResearchGroupName());
      tvTimeFrom.setText(experiment.getStartTime());
      tvTimeTo.setText(experiment.getEndTime());
      tvSubjectName.setText(experiment.getSubjectName());
      tvSubjectSurname.setText(experiment.getSubjectSurname());
    }
  }

  public void selectExperiment(View view) {
    startActivityForResult(new Intent(this, SelectExperimentActivity.class), REQUEST_SELECT_EXPERIMENT);
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case REQUEST_SELECT_EXPERIMENT:
        if(resultCode == RESULT_OK) {
          setExperiment((Experiment) data.getParcelableExtra(SelectExperimentActivity.EXTRA_EXPERIMENT));
        }
        break;
  
      default:
        super.onActivityResult(requestCode, resultCode, data);
        break;
    }
  }
  
  public void uploadData(View view) {
    if(taskUpload != null) {
      AndroidUtils.toast(this, R.string.fail_data_already_uploading);
      return;
    }

    if(experiment == null) {
      AndroidUtils.toast(this, R.string.fail_select_experiment);
      return;
    }

    if(data != null) {
      taskUpload = new UploadGenericParametersTask(userProfile.getEmail(), userProfile.getEegbasePassword(), experiment.getExperimentId(), data);
      taskUpload.execute();
    }
    else if(uploadHelper != null) {
      taskUpload = new UploadGenericParametersTask(userProfile.getEmail(), userProfile.getEegbasePassword(), experiment.getExperimentId(), uploadHelper);
      taskUpload.execute();
    }
  }
  
  protected void setExperiment(Experiment experiment) {
    this.experiment = experiment;
    setExperimentInfo(experiment);
  }
  
  
  
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
    
    @Override
    protected void onPostExecute(AsyncTaskResult<AddExperimentDataResult> result) {
      //error
      if(result.getError() != null) {
        if(result.getError() instanceof WrongCredentialsException) {
          AndroidUtils.toast(UploadGenericParametersActivity.this, R.string.fail_login_credentials);
        }
        else {
          AndroidUtils.toast(UploadGenericParametersActivity.this, R.string.fail_communication);
        }
      }
      //success
      else {
        AndroidUtils.toast(UploadGenericParametersActivity.this, R.string.success_data_uploaded);
      }
      taskUpload = null;
    }
  }
}
