package cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure;

import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListView;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.data.database.AutoSyncTable;
import cz.zcu.kiv.mobile.logger.data.database.Database;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.EntryNotFoundException;
import cz.zcu.kiv.mobile.logger.data.types.AutoSync;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.eegbase.data.get_experiment_list.Experiment;
import cz.zcu.kiv.mobile.logger.eegbase.gen_par_upload.SelectExperimentActivity;
import cz.zcu.kiv.mobile.logger.eegbase.gen_par_upload.UploadGenericParametersActivity;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.BloodPressureMeasurementDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.IExperimentParametersUploadHelper;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


//TODO duplicit code - use ADataListFragment for this...
public class BloodPressureListActivity extends ListActivity implements LoaderCallbacks<AsyncTaskResult<Cursor>> {
  private static final String TAG = BloodPressureListActivity.class.getSimpleName();

  private static final int REQUEST_SELECT_EXPERIMENT = 121;
  private static final int LOADER_BPM_LIST = 10;

  private CursorAdapter dataAdapter;
  private Profile userProfile;
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_blood_pressure_list);
    
    userProfile = Application.getInstance().getUserProfileOrLogIn();
    
    if(userProfile == null){  //TODO inherit...
      AndroidUtils.toast(this, R.string.alert_must_be_logged_in);
      Log.e(TAG, "User must be logged in.");
      finish();
      return;
    }
    
    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    dataAdapter = new BloodPressureAdapter(this, null, 0);
    setListAdapter(dataAdapter);
    
    getLoaderManager().initLoader(LOADER_BPM_LIST, null, this);
    
    AutoSync autosync = loadAutosync();
    setAutosyncInfo(autosync);
  }

  private AutoSync loadAutosync() {
    AutoSyncTable db = Application.getInstance().getDatabase().getAutoSyncTable();
    AutoSync autosync = null;
    try {
      autosync = db.getAutoSync(userProfile.getId(), Database.TABLE_ID_BP);
    }
    catch(EntryNotFoundException e) {
      autosync = null;
    }
    catch(DatabaseException e) {
      AndroidUtils.toast(this, R.string.fail_load_autosync_info);
      Log.e(TAG, "Failed to load autosync info.", e);
    }
    return autosync;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.blood_pressure_list, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_upload:
        triggerUpload();
        return true;
        
      case R.id.action_auto_sync:
        startActivityForResult(new Intent(this, SelectExperimentActivity.class), REQUEST_SELECT_EXPERIMENT);
        return true;

      default: return super.onOptionsItemSelected(item);
    }
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case REQUEST_SELECT_EXPERIMENT:
        if(resultCode == Activity.RESULT_OK) {
          Experiment experiment = data.getParcelableExtra(SelectExperimentActivity.EXTRA_EXPERIMENT);
          handleAutoSyncChange(experiment);
        }
        break;
  
      default:
        super.onActivityResult(requestCode, resultCode, data);
        break;
    }
  }

  private void handleAutoSyncChange(Experiment experiment) {
    AutoSyncTable db = Application.getInstance().getDatabase().getAutoSyncTable();
    try {
      db.deleteAutoSync(userProfile.getId(), Database.TABLE_ID_BP);
      setAutosyncInfo(null);
      
      if(experiment != null) {
        AutoSync newAutosync = new AutoSync(Database.TABLE_ID_BP, experiment.getExperimentId(), experiment.getExperimentId() + " " + experiment.getScenarioName());
        db.addAutoSync(userProfile.getId(), newAutosync);
        setAutosyncInfo(newAutosync);
      }
    }
    catch (DatabaseException e) {
      AndroidUtils.toast(this, R.string.fail_change_autosync);
      Log.e(TAG, "Failed to change autosync.", e);
    }
  }

  private void setAutosyncInfo(AutoSync autosync) {
    getActionBar().setSubtitle(
        (autosync == null) ? getString(R.string.no_autosync) : autosync.getExperimentName());
  }

  private void triggerUpload() {
    long[] selected = getListView().getCheckedItemIds();
    if(selected.length == 0){
      AndroidUtils.toast(this, R.string.fail_no_upload_data_selected);
      return;
    }
    
    String parameterName = Application.getPreferences().getString("pref_gen_par_name_bp", "Blood pressure measurements");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    IExperimentParametersUploadHelper uploadHelper = new BloodPressureMeasurementDbUploadHelper(parameterName, 0.0, selected, append);
    
    Intent uploadIntent = new Intent(this, UploadGenericParametersActivity.class);
    uploadIntent.putExtra(UploadGenericParametersActivity.PARAM_GENERIC_PARAMETERS_UPLOAD_HELPER, uploadHelper);
    startActivity(uploadIntent);
  }

  @Override
  public Loader<AsyncTaskResult<Cursor>> onCreateLoader(int id, Bundle args) {
    return new BPMeasurementLoader(this, userProfile.getId());
  }

  @Override
  public void onLoadFinished(Loader<AsyncTaskResult<Cursor>> loader, AsyncTaskResult<Cursor> data) {
    if(loader.getId() == LOADER_BPM_LIST) {
      if(data.getError() != null) {
        AndroidUtils.toast(this, R.string.fail_load_measurements);
      }
      else {
        dataAdapter.swapCursor(data.getResult());
      }
    }
  }

  @Override
  public void onLoaderReset(Loader<AsyncTaskResult<Cursor>> data) {
    dataAdapter.swapCursor(null);
  }
}
