package cz.zcu.kiv.mobile.logger.devices;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CursorAdapter;
import android.widget.ListView;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.database.ARecordTable;
import cz.zcu.kiv.mobile.logger.data.database.AutoSyncTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.EntryNotFoundException;
import cz.zcu.kiv.mobile.logger.data.types.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.data.types.AutoSync;
import cz.zcu.kiv.mobile.logger.eegbase.types.get_experiment_list.Experiment;
import cz.zcu.kiv.mobile.logger.eegbase.upload.gen_par.SelectExperimentActivity;
import cz.zcu.kiv.mobile.logger.eegbase.upload.gen_par.UploadGenericParametersActivity;
import cz.zcu.kiv.mobile.logger.eegbase.upload.helpers.IExperimentParametersUploadHelper;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;
import cz.zcu.kiv.mobile.logger.utils.DialogUtils;


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
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    registerForContextMenu(getListView());
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
        
      case R.id.action_delete_selected:
        DialogUtils.showDeleteDialog(getActivity(), R.string.dialog_delete_selected_records_message,  new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            removeSelectedRecords();
            dialog.dismiss();
          }
        });
        return true;
        
      case R.id.action_de_select_all:
        switchSelection();
        return true;
        
      case R.id.action_delete_uploaded:
        DialogUtils.showDeleteDialog(getActivity(), R.string.dialog_delete_uploaded_records_message,  new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            removeUploadedRecords();
            dialog.dismiss();
          }
        });
        return true;
        
      case R.id.action_delete_all:
        DialogUtils.showDeleteDialog(getActivity(), R.string.dialog_delete_all_records_message,  new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            removeAllRecords();
            dialog.dismiss();
          }
        });
        return true;

      default: return super.onOptionsItemSelected(item);
    }
  }
  
  private void switchSelection() {
    ListView list = getListView();
    boolean check = list.getCheckedItemCount() == 0;
    
    for(int i = 0; i < dataAdapter.getCount(); i++) {
      list.setItemChecked(i, check);
    }
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    getActivity().getMenuInflater().inflate(R.menu.data_list_context, menu);
  }
  
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    final long recordID = ((AdapterContextMenuInfo) item.getMenuInfo()).id;
    
    switch (item.getItemId()) {
      case R.id.context_delete:
        DialogUtils.showDeleteDialog(getActivity(), R.string.dialog_delete_record_message,  new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            removeRecord(recordID);
            dialog.dismiss();
          }
        });
        return true;
  
      default:
        return super.onContextItemSelected(item);
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

  private void removeRecord(long recordID) {
    try {
      getDatabase().deleteRecord(recordID);
    }
    catch (DatabaseException e) {
      Log.e(TAG, "Failed to delete record: id=" + recordID, e);
      AndroidUtils.toast(getActivity(), R.string.fail_delete_records);
      return;
    }
  }

  private void removeSelectedRecords() {
    try {
      getDatabase().deleteRecords(getListView().getCheckedItemIds());
    }
    catch (DatabaseException e) {
      Log.e(TAG, "Failed to delete selected records", e);
      AndroidUtils.toast(getActivity(), R.string.fail_delete_records);
      return;
    }
  }

  private void removeUploadedRecords() {
    try {
      getDatabase().deleteUploadedRecords(userID);
    }
    catch (DatabaseException e) {
      Log.e(TAG, "Failed to delete uploaded records", e);
      AndroidUtils.toast(getActivity(), R.string.fail_delete_records);
      return;
    }
  }

  private void removeAllRecords() {
    try {
      getDatabase().deleteAllRecords(userID);
    }
    catch (DatabaseException e) {
      Log.e(TAG, "Failed to delete all records", e);
      AndroidUtils.toast(getActivity(), R.string.fail_delete_records);
      return;
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
        //TODO spustit synchronizaci, pokud je vyplá? jinak by se muselo změnit profil
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
  
  private int getTableID() {
    return getDatabase().getTableID();
  }

  
  protected abstract int getLoaderID();
  protected abstract ARecordTable<?> getDatabase();
  protected abstract IExperimentParametersUploadHelper getUploadHelper(long[] selected);
  protected abstract CursorAdapter getDataAdapter(Context context);
}
