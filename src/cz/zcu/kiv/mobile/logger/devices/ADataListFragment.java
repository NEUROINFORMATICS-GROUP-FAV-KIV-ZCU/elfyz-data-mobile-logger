package cz.zcu.kiv.mobile.logger.devices;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.eegbase.UploadGenericParametersActivity;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.IExperimentParametersUploadHelper;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public abstract class ADataListFragment extends ListFragment implements LoaderCallbacks<AsyncTaskResult<Cursor>> {
  protected static final String ARG_USER_ID = "user.id";
  
  protected static final int RESULT_UPLOAD = 22;
  
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
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragments_data_list, container, false);
    return rootView;
  }
  
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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

      default: return super.onOptionsItemSelected(item);
    }
  }

  private void triggerUpload() {
    long[] selected = getListView().getCheckedItemIds();
    if(selected.length == 0){
      AndroidUtils.toast(getActivity(), "No records selected for upload.");
      return;
    }
    
    //TODO configurable by user
    IExperimentParametersUploadHelper uploadHelper = getUploadHelper(selected);
    
    Intent uploadIntent = new Intent(getActivity(), UploadGenericParametersActivity.class);
    uploadIntent.putExtra(UploadGenericParametersActivity.PARAM_GENERIC_PARAMETERS_UPLOAD_HELPER, uploadHelper);
    startActivityForResult(uploadIntent, RESULT_UPLOAD);  //TODO zpracovani vysledku?
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
  protected abstract IExperimentParametersUploadHelper getUploadHelper(long[] selected);
  protected abstract CursorAdapter getDataAdapter(Context context);
}
