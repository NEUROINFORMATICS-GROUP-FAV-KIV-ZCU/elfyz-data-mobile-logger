package cz.zcu.kiv.mobile.logger.eegbase;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.ExperimentParametersData;
import cz.zcu.kiv.mobile.logger.eegbase.data.get_experiment_list.ExperimentList;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.CommunicationException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.WrongCredentialsException;
import cz.zcu.kiv.mobile.logger.eegbase.tasks.UploadGenericParametersTask;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.IExperimentParametersUploadHelper;
import cz.zcu.kiv.mobile.logger.profiles.ProfileActivity;
import cz.zcu.kiv.mobile.logger.retain.ExperimentRetainFragment;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public class UploadGenericParametersActivity extends ListActivity {
  private static final String TAG = UploadGenericParametersActivity.class.getSimpleName();
  
  public static final String PARAM_GENERIC_PARAMETERS = "generic.parameters";
  public static final String PARAM_GENERIC_PARAMETERS_UPLOAD_HELPER = "generis.parameters.upload.helper";
  
  private static final String FRAGMENT_RETAIN = "experiment.retain.fragment";

  private Profile userProfile;
  
  private ExperimentParametersData data;
  private IExperimentParametersUploadHelper uploadHelper;
  
  private View listStuff;
  private View progress;
  
  private LoadExperimentsTask taskExperiments = null;
  private UploadGenericParametersTask taskUpload = null;
  
  private ExperimentsAdapter adapter;
  private ExperimentRetainFragment retainFragment;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_upload_generic_parameters);
    
    userProfile = Application.getInstance().getUserProfileOrLogIn();
    
    if(userProfile == null){
      AndroidUtils.toast(this, R.string.alert_must_be_logged_in);
      Log.e(TAG, "User must be logged in.");
      finish();
      return;
    }
    
    if(userProfile.getEegbasePassword() == null) {
      AndroidUtils.toast(this, R.string.alert_pair_eegbase_profile);
      Log.e(TAG, "Profile is not paired with EEGbase.");
      
      Intent intent = new Intent(this, ProfileActivity.class);
      intent.putExtra(ProfileActivity.EXTRA_PROFILE_ID, userProfile.getId());
      startActivity(intent);
      
      finish();
      return;
    }
    
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
    }
    
    listStuff = findViewById(R.id.list_stuff);
    progress = findViewById(R.id.progress);
    
    adapter = new ExperimentsAdapter(this);
    retainFragment = (ExperimentRetainFragment) getFragmentManager().findFragmentByTag(FRAGMENT_RETAIN);
    if(retainFragment == null) {
      retainFragment = new ExperimentRetainFragment();
      getFragmentManager().beginTransaction().add(retainFragment, FRAGMENT_RETAIN).commit();

      refreshExperiments(null);
    }
    else {
      adapter.setExperiments(retainFragment.getExperiments());
    }
    getListView().setAdapter(adapter);
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    retainFragment.setExperiments(adapter.getExperiments());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.upload_generic_parameters, menu);
    return true;
  }
  
  public void refreshExperiments(View view) {
    if(taskExperiments != null) {
      AndroidUtils.toast(this, R.string.fail_experiments_already_downloading);
      return;
    }
    
    setProgress(true);
    
    taskExperiments = new LoadExperimentsTask();
    taskExperiments.execute();
  }

  public void uploadData(View view) {
    if(taskUpload != null) {
      AndroidUtils.toast(this, R.string.fail_data_already_uploading);
      return;
    }

    int selected = getListView().getCheckedItemPosition();
    if(selected == AdapterView.INVALID_POSITION) {
      AndroidUtils.toast(this, R.string.fail_select_experiment);
      return;
    }

    String experimentID = adapter.getExperiment(selected).getExperimentId();
    
    if(data != null) {
      taskUpload = new UploadGenericParametersTask(userProfile.getEmail(), userProfile.getEegbasePassword(), experimentID, data);
      taskUpload.execute();
    }
    else if(uploadHelper != null) {
      taskUpload = new UploadGenericParametersTask(userProfile.getEmail(), userProfile.getEegbasePassword(), experimentID, uploadHelper);
      taskUpload.execute();
    }
  }
  
  private void setProgress(boolean working) {
    if(working) {
      listStuff.setVisibility(View.GONE);
      progress.setVisibility(View.VISIBLE);
    }
    else {
      listStuff.setVisibility(View.VISIBLE);
      progress.setVisibility(View.GONE);
    }
  }
  
  
  
  private class LoadExperimentsTask extends AsyncTask<Void, Void, AsyncTaskResult<ExperimentList>> {

    @Override
    protected AsyncTaskResult<ExperimentList> doInBackground(Void... params) {
      AsyncTaskResult<ExperimentList> result = new AsyncTaskResult<ExperimentList>();
      try {
        ExperimentList list = EegbaseRest.getMyExperiments(userProfile.getEmail(), userProfile.getEegbasePassword());
        result.setResult(list);
      }
      catch (WrongCredentialsException e) {
        result.setError(e);
      }
      catch (CommunicationException e) {
        result.setError(e);
      }
      return result;
    }
    
    
    @Override
    protected void onPostExecute(AsyncTaskResult<ExperimentList> result) {
      //error
      if(result.getError() != null) {
        if(result.getError() instanceof WrongCredentialsException) {
          AndroidUtils.toast(UploadGenericParametersActivity.this, R.string.fail_login_credentials);
        }
        else {
          AndroidUtils.toast(UploadGenericParametersActivity.this, R.string.fail_communication);
        }
        return;
      }
      //success
      else {
        adapter.setExperiments(result.getResult().getExperiments());
      }
      taskExperiments = null;
      setProgress(false);
    }
  }
}
