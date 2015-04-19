package cz.zcu.kiv.mobile.logger.devices;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.data.database.AutoSyncTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.EntryNotFoundException;
import cz.zcu.kiv.mobile.logger.data.types.AutoSync;
import cz.zcu.kiv.mobile.logger.eegbase.data.get_experiment_list.Experiment;
import cz.zcu.kiv.mobile.logger.eegbase.gen_par_upload.SelectExperimentActivity;
import cz.zcu.kiv.mobile.logger.eegbase.gen_par_upload.UploadGenericParametersActivity;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.IExperimentParametersUploadHelper;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public abstract class ADataListFragment extends ListFragment implements LoaderCallbacks<AsyncTaskResult<Cursor>> {
  private static final String TAG = ADataListFragment.class.getSimpleName();
  
  private static final int REQUEST_SELECT_EXPERIMENT = 121;
  
  protected static final String ARG_USER_ID = "user.id";
  
  protected long userID;
  protected int loaderID;
  protected CursorAdapter dataAdapter;
  
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    userID = getArguments().getLong(ARG_USER_ID, -1L);
    loaderID = getLoaderID();
    getLoaderManager().initLoader(loaderID, null, this);
    setHasOptionsMenu(true);
  }
  
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    
    dataAdapter = getDataAdapter(activity);
    setListAdapter(dataAdapter);
    
    AutoSync autosync = loadAutosync();
    setAutosyncInfo(autosync);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragments_data_list, container, false);
    return rootView;
  }
  
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.data_list_fragment, menu);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_upload:
        triggerUpload();
        return true;
        
      case R.id.action_auto_sync:
        startActivityForResult(new Intent(getActivity(), SelectExperimentActivity.class), REQUEST_SELECT_EXPERIMENT);
        return true;

      default: return super.onOptionsItemSelected(item);
    }
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
      db.deleteAutoSync(userID, getTableID());
      setAutosyncInfo(null);
      
      if(experiment != null) {
        AutoSync newAutosync = new AutoSync(getTableID(), experiment.getExperimentId(), experiment.getExperimentId() + " " + experiment.getScenarioName());
        db.addAutoSync(userID, newAutosync);
        setAutosyncInfo(newAutosync);
      }
    }
    catch (DatabaseException e) {
      AndroidUtils.toast(getActivity(), R.string.fail_change_autosync);
      Log.e(TAG, "Failed to change autosync.", e);
    }
  }

  private AutoSync loadAutosync() {
    AutoSyncTable db = Application.getInstance().getDatabase().getAutoSyncTable();
    AutoSync autosync = null;
    try {
      autosync = db.getAutoSync(userID, getTableID());
    }
    catch(EntryNotFoundException e) {
      autosync = null;
    }
    catch(DatabaseException e) {
      AndroidUtils.toast(getActivity(), R.string.fail_load_autosync_info);
      Log.e(TAG, "Failed to load autosync info.", e);
    }
    return autosync;
  }

  private void setAutosyncInfo(AutoSync autosync) {
    getActivity().getActionBar().setSubtitle(
        (autosync == null) ? getString(R.string.no_autosync) : autosync.getExperimentName());
  }

  private void triggerUpload() {
    long[] selected = getListView().getCheckedItemIds();
    if(selected.length == 0){
      AndroidUtils.toast(getActivity(), R.string.fail_no_upload_data_selected);
      return;
    }
    
    IExperimentParametersUploadHelper uploadHelper = getUploadHelper(selected);
    
    Intent uploadIntent = new Intent(getActivity(), UploadGenericParametersActivity.class);
    uploadIntent.putExtra(UploadGenericParametersActivity.PARAM_GENERIC_PARAMETERS_UPLOAD_HELPER, uploadHelper);
    startActivity(uploadIntent);
  }


  @Override
  public void onLoadFinished(Loader<AsyncTaskResult<Cursor>> loader, AsyncTaskResult<Cursor> data) {
    if(loader.getId() == loaderID) {
      if(data.getError() != null) {
        AndroidUtils.toast(getActivity(), R.string.fail_load_measurements);
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

  
  protected abstract int getLoaderID();
  protected abstract int getTableID();
  protected abstract IExperimentParametersUploadHelper getUploadHelper(long[] selected);
  protected abstract CursorAdapter getDataAdapter(Context context);
}
