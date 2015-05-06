package cz.zcu.kiv.mobile.logger.eegbase.upload.gen_par;

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
import cz.zcu.kiv.mobile.logger.data.types.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.eegbase.EegbaseRest;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.CommunicationException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.WrongCredentialsException;
import cz.zcu.kiv.mobile.logger.eegbase.types.get_experiment_list.Experiment;
import cz.zcu.kiv.mobile.logger.eegbase.types.get_experiment_list.ExperimentList;
import cz.zcu.kiv.mobile.logger.profiles.ProfileActivity;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public class SelectExperimentActivity extends ListActivity {
  private static final String TAG = SelectExperimentActivity.class.getSimpleName();
  
  public static final String EXTRA_EXPERIMENT = "extra.experiment";
  
  private static final String FRAGMENT_RETAIN = "experiment.retain.fragment";
  
  private View listStuff;
  private View progress;

  private Profile userProfile;
  
  private ExperimentsAdapter adapter;
  private ExperimentRetainFragment retainFragment;
  
  private LoadExperimentsTask taskExperiments = null;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select_experiment);
    
    userProfile = Application.getInstance().getUserProfileOrLogIn();
    
    if(userProfile == null) {
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
    getMenuInflater().inflate(R.menu.select_experiment, menu);
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
  
  public void selectExperiment(View view) {
    int selected = getListView().getCheckedItemPosition();
    if(selected == AdapterView.INVALID_POSITION) {
      AndroidUtils.toast(this, R.string.fail_select_experiment);
      return;
    }

    Experiment experiment = adapter.getExperiment(selected);
    
    Intent result = new Intent();
    result.putExtra(EXTRA_EXPERIMENT, experiment);
    
    setResult(RESULT_OK, result);
    finish();
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
          AndroidUtils.toast(SelectExperimentActivity.this, R.string.fail_login_credentials);
        }
        else {
          AndroidUtils.toast(SelectExperimentActivity.this, R.string.fail_communication);
        }
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
