package cz.zcu.kiv.mobile.logger.devices.fora.glucose;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.CursorAdapter;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure.BloodPressureListActivity;
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
