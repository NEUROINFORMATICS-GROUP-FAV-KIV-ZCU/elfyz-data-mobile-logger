package cz.zcu.kiv.mobile.logger.devices.fora.glucose;

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
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure.BloodPressureListActivity;
import cz.zcu.kiv.mobile.logger.eegbase.UploadGenericParametersActivity;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.GlucoseMeasurementDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.IExperimentParametersUploadHelper;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public class GlucoseMeterListActivity extends ListActivity implements LoaderCallbacks<AsyncTaskResult<Cursor>> {
  private static final String TAG = BloodPressureListActivity.class.getSimpleName();
  
  private static final int LOADER_GM_LIST = 10;

  private CursorAdapter dataAdapter;
  private Profile userProfile;
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_glucose_list);
    
    userProfile = Application.getInstance().getUserProfileOrLogIn();
    
    if(userProfile == null) {
      AndroidUtils.toast(this, R.string.alert_must_be_logged_in);
      Log.e(TAG, "User must be logged in.");
      finish();
      return;
    }

    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    dataAdapter = new GlucoseMeterAdapter(this, null, 0);
    setListAdapter(dataAdapter);
    
    getLoaderManager().initLoader(LOADER_GM_LIST, null, this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.glucose_list, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_upload:
        triggerUpload();
        return true;

      default: return super.onOptionsItemSelected(item);
    }
  }

  private void triggerUpload() {
    long[] selected = getListView().getCheckedItemIds();
    if(selected.length == 0){
      AndroidUtils.toast(this, "No records selected for upload.");
      return;
    }

    String parameterName = Application.getPreferences().getString("pref_gen_par_name_gm", "Glucose measurements");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    IExperimentParametersUploadHelper uploadHelper = new GlucoseMeasurementDbUploadHelper(parameterName, 0.0, selected, append);
    
    Intent uploadIntent = new Intent(this, UploadGenericParametersActivity.class);
    uploadIntent.putExtra(UploadGenericParametersActivity.PARAM_GENERIC_PARAMETERS_UPLOAD_HELPER, uploadHelper);
    startActivity(uploadIntent);
  }

  @Override
  public Loader<AsyncTaskResult<Cursor>> onCreateLoader(int id, Bundle args) {
    return new GMeasurementLoader(this, userProfile.getId());
  }

  @Override
  public void onLoadFinished(Loader<AsyncTaskResult<Cursor>> loader, AsyncTaskResult<Cursor> data) {
    if(loader.getId() == LOADER_GM_LIST) {
      if(data.getError() != null) {
        AndroidUtils.toast(this, R.string.fail_load_measurements);
      }
      else {
        dataAdapter.swapCursor(data.getResult());
      }
    }
  }

  @Override
  public void onLoaderReset(Loader<AsyncTaskResult<Cursor>> arg0) {
    dataAdapter.swapCursor(null);
  }
}
