package cz.zcu.kiv.mobile.logger.devices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.database.loaders.AAnalysisLoader;
import cz.zcu.kiv.mobile.logger.data.types.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.profiles.DatePickerFragment;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;
import cz.zcu.kiv.mobile.logger.utils.DateUtils;


public abstract class AAnalysisFragment extends Fragment implements LoaderCallbacks<AsyncTaskResult<Cursor>> {
  private static final String TAG = AAnalysisFragment.class.getSimpleName();
  
  protected static final String ARG_USER_ID = "user.id";
  
  private static final String STATE_DATE_FROM = "state.date.from";
  private static final String STATE_DATE_TO = "state.date.to";
  
  protected static final DateFormat timeFormat =
      SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
  protected static final DateFormat dateFormat =
      SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);

  protected Date dateFrom;
  protected Date dateTo;
  
  private AAnalysisLoader loader;
  private int loaderID;

  private long userID;

  protected Button bDateFrom;
  protected Button bDateTo;
  
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    userID = getArguments().getLong(ARG_USER_ID, -1L);
    
    long milisTo = -1L;
    long milisFrom = -1L;
    if(savedInstanceState != null) milisTo = savedInstanceState.getLong(STATE_DATE_TO);
    dateTo = milisTo < 0L ? new Date() : new Date(milisTo);
    if(savedInstanceState != null) milisFrom = savedInstanceState.getLong(STATE_DATE_FROM);
    dateFrom = milisFrom < 0L ? DateUtils.addDays(dateTo, -7) : new Date(milisFrom);
    
    loaderID = getLoaderID();
    getLoaderManager().initLoader(loaderID, null, this);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(STATE_DATE_FROM, dateFrom.getTime());
    outState.putLong(STATE_DATE_TO, dateTo.getTime());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(getLayoutResourceID(), container, false);
    bDateFrom = (Button) rootView.findViewById(R.id.b_date_from);
    bDateTo = (Button) rootView.findViewById(R.id.b_date_to);
    initButtons();
    return rootView;
  }

  private void initButtons() {
    bDateFrom.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        selectDateFrom();
      }
    });
    bDateTo.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        selectDateTo();
      }
    });
    refreshButtons();
    refreshAnalysis();
  }

  @Override
  public void onLoadFinished(Loader<AsyncTaskResult<Cursor>> loader, AsyncTaskResult<Cursor> data) {
    if(loader.getId() == loaderID) {
      if(data.getError() != null) {
        AndroidUtils.toast(getActivity(), R.string.fail_load_measurements);
      }
      else {
        processCursor(data.getResult());
      }
    }
  }

  private void processCursor(Cursor cursor) {
    try {
      analyze(cursor);
    }
    catch (Exception e) {
      AndroidUtils.toast(getActivity(), "Failed to analyze data.");
      Log.e(TAG, "Failed to analyze data", e);
    }
    finally {
      CloseUtils.close(cursor);
    }
  }
  
  public void selectDateFrom() {
    DatePickerFragment dialog = new DatePickerFragment();
    if(dateFrom != null) {
      Bundle args = new Bundle(1);
      args.putLong(DatePickerFragment.ARG_INITIAL_DATE, dateFrom.getTime());
      dialog.setArguments(args);
    }
    dialog.setListener(new OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        dateFrom = DateUtils.getDate(year, monthOfYear, dayOfMonth).getTime();
        refreshButtons();
        refreshAnalysis();
      }
    });
    dialog.show(getFragmentManager(), "datePicker");
  }
  
  public void selectDateTo() {
    DatePickerFragment dialog = new DatePickerFragment();
    if(dateTo != null) {
      Bundle args = new Bundle(1);
      args.putLong(DatePickerFragment.ARG_INITIAL_DATE, dateTo.getTime());
      dialog.setArguments(args);
    }
    dialog.setListener(new OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        dateTo = DateUtils.getDate(year, monthOfYear, dayOfMonth).getTime();
        refreshButtons();
        refreshAnalysis();
      }
    });
    dialog.show(getFragmentManager(), "datePicker");
  }
  
  private void refreshButtons() {
    bDateFrom.setText(dateFormat.format(dateFrom));
    bDateTo.setText(dateFormat.format(dateTo));
  }
  
  private void refreshAnalysis() {
    if(loader != null) {
      loader.triggerLoad(dateFrom, dateTo);
    }
  }

  
  @Override
  public void onLoaderReset(Loader<AsyncTaskResult<Cursor>> result) { }
  
  @Override
  public Loader<AsyncTaskResult<Cursor>> onCreateLoader(int arg0, Bundle arg1) {
    loader = getAnalysisLoader(getActivity(), userID);
    loader.setDates(dateFrom, dateTo);
    return loader;
  }
  
  protected abstract int getLoaderID();
  protected abstract int getLayoutResourceID();
  protected abstract AAnalysisLoader getAnalysisLoader(Context context, long userID);
  protected abstract void analyze(Cursor cursor) throws Exception;
}
