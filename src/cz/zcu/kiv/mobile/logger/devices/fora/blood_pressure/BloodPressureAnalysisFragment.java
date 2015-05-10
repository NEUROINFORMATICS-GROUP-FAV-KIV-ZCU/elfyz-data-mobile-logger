package cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog.OnDateSetListener;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.database.BloodPressureMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.types.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.profiles.DatePickerFragment;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;
import cz.zcu.kiv.mobile.logger.utils.DateUtils;


public class BloodPressureAnalysisFragment extends Fragment implements LoaderCallbacks<AsyncTaskResult<Cursor>> {
  private static final String TAG = BloodPressureAnalysisFragment.class.getSimpleName();
  private static final int LOADER_ID = 71;
  
  private static final String ARG_USER_ID = "user.id";
  private static final String STATE_DATE_FROM = "state.date.from";
  private static final String STATE_DATE_TO = "state.date.to";
  
  private static final int COLOR_LIMIT_HIGH = Color.RED;
  private static final int COLOR_LIMIT_PREHIGH = Color.YELLOW;
  private static final int COLOR_LIMIT_OPTIMAL = Color.GREEN;
  private static final int COLOR_LIMIT_LOW = Color.DKGRAY;
  private static final int COLOR_SYS = Color.CYAN;
  private static final int COLOR_DIA = Color.BLUE;
  private static final int COLOR_MEAN = Color.LTGRAY;
  private static final int COLOR_HR = Color.MAGENTA;
  
  private static final DateFormat timeFormat =
      SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
  private static final DateFormat dateFormat =
      SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);

  private Date dateFrom;
  private Date dateTo;
  private BPMeasurementAnalysisLoader loader;
  
  private long userID;
  private LineChart chart;
  
  private TextView tvTrend;
  private TextView tvAverage;
  private TextView tvCount;
  private TextView tvMax;
  private TextView tvMaxTime;
  private TextView tvMin;
  private TextView tvMinTime;

  private Button bDateFrom;
  private Button bDateTo;
  private ImageButton bRefresh;
  
  
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
    
    getLoaderManager().initLoader(LOADER_ID, null, this);
  }
  
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(STATE_DATE_FROM, dateFrom.getTime());
    outState.putLong(STATE_DATE_TO, dateTo.getTime());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_analysis_bp, container, false);
    chart = (LineChart) rootView.findViewById(R.id.chart);
    tvTrend = (TextView) rootView.findViewById(R.id.tv_stats_trend);
    tvAverage = (TextView) rootView.findViewById(R.id.tv_stats_average);
    tvCount = (TextView) rootView.findViewById(R.id.tv_stats_count);
    tvMax = (TextView) rootView.findViewById(R.id.tv_stats_max);
    tvMaxTime = (TextView) rootView.findViewById(R.id.tv_stats_max_time);
    tvMin = (TextView) rootView.findViewById(R.id.tv_stats_min);
    tvMinTime = (TextView) rootView.findViewById(R.id.tv_stats_min_time);
    bDateFrom = (Button) rootView.findViewById(R.id.b_date_from);
    bDateTo = (Button) rootView.findViewById(R.id.b_date_to);
    bRefresh = (ImageButton) rootView.findViewById(R.id.b_refresh);
    initButtons();
    initChart();
    return rootView;
  }
  
  private void initButtons() {
    bDateFrom.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        selectDateFrom(v);
      }
    });
    bDateTo.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        selectDateTo(v);
      }
    });
    bRefresh.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        refreshAnalysis(v);
      }
    });
    refreshButtons();
  }

  private void initChart() {
    chart.setNoDataTextDescription(Application.getStringResource(R.string.no_data));
    chart.setDragEnabled(true);
    chart.setDrawGridBackground(true);
    
    YAxis leftAxis = chart.getAxisLeft();
    leftAxis.setDrawLimitLinesBehindData(true);
      LimitLine ll = new LimitLine(140f, Application.getStringResource(R.string.bp_level_high));
        ll.setLineColor(COLOR_LIMIT_HIGH);
        ll.setLineWidth(4f);
        ll.setTextColor(COLOR_LIMIT_HIGH);
        ll.setTextSize(10f);
        ll.setLabelPosition(LimitLabelPosition.POS_LEFT);
      leftAxis.addLimitLine(ll);
      ll = new LimitLine(139f, Application.getStringResource(R.string.bp_level_prehigh));
        ll.setLineColor(COLOR_LIMIT_PREHIGH);
        ll.setLineWidth(4f);
        ll.setTextColor(COLOR_LIMIT_PREHIGH);
        ll.setTextSize(10f);
        ll.setLabelPosition(LimitLabelPosition.POS_RIGHT);
      leftAxis.addLimitLine(ll);
      ll = new LimitLine(120f, Application.getStringResource(R.string.bp_level_prehigh));
        ll.setLineColor(COLOR_LIMIT_PREHIGH);
        ll.setLineWidth(4f);
        ll.setTextColor(COLOR_LIMIT_PREHIGH);
        ll.setTextSize(10f);
        ll.setLabelPosition(LimitLabelPosition.POS_LEFT);
      leftAxis.addLimitLine(ll);
      ll = new LimitLine(119f, Application.getStringResource(R.string.bp_level_optimal));
        ll.setLineColor(COLOR_LIMIT_OPTIMAL);
        ll.setLineWidth(4f);
        ll.setTextColor(COLOR_LIMIT_OPTIMAL);
        ll.setTextSize(10f);
        ll.setLabelPosition(LimitLabelPosition.POS_RIGHT);
      leftAxis.addLimitLine(ll);
      ll = new LimitLine(90f, Application.getStringResource(R.string.bp_level_optimal));
        ll.setLineColor(COLOR_LIMIT_OPTIMAL);
        ll.setLineWidth(4f);
        ll.setTextColor(COLOR_LIMIT_OPTIMAL);
        ll.setTextSize(10f);
        ll.setLabelPosition(LimitLabelPosition.POS_LEFT);
      leftAxis.addLimitLine(ll);
      ll = new LimitLine(89f, Application.getStringResource(R.string.bp_level_low));
        ll.setLineColor(COLOR_LIMIT_LOW);
        ll.setLineWidth(4f);
        ll.setTextColor(COLOR_LIMIT_LOW);
        ll.setTextSize(10f);
        ll.setLabelPosition(LimitLabelPosition.POS_RIGHT);
      leftAxis.addLimitLine(ll);
  }


  @Override
  public void onLoadFinished(Loader<AsyncTaskResult<Cursor>> loader, AsyncTaskResult<Cursor> data) {
    if(loader.getId() == LOADER_ID) {
      if(data.getError() != null) {
        AndroidUtils.toast(getActivity(), R.string.fail_load_measurements);
      }
      else {
        analyze(data.getResult());
      }
    }
  }

  private void analyze(Cursor cursor) {
    try {
      List<Entry> valSys = new ArrayList<Entry>();
      List<Entry> valDia = new ArrayList<Entry>();
      List<Entry> valMean = new ArrayList<Entry>();
      List<Entry> valHR = new ArrayList<Entry>();
      List<String> labels = new ArrayList<String>();
      
      int iSys = cursor.getColumnIndex(BloodPressureMeasurementTable.COLUMN_SYSTOLIC);
      int iDia = cursor.getColumnIndex(BloodPressureMeasurementTable.COLUMN_DIASTOLIC);
      int iMean = cursor.getColumnIndex(BloodPressureMeasurementTable.COLUMN_MEAN_PRESSURE);
      int iHR = cursor.getColumnIndex(BloodPressureMeasurementTable.COLUMN_HEART_RATE);
      int iTime = cursor.getColumnIndex(BloodPressureMeasurementTable.COLUMN_TIME);
      
      int count = 0;
      int prev = 0;
      int trend = 0;
      int sum = 0;
      int max = Integer.MIN_VALUE;
      long maxTime = 0L;
      int min = Integer.MAX_VALUE;
      long minTime = 0L;
      
      while(cursor.moveToNext()) {
        int sys = cursor.getInt(iSys);
        long time = cursor.getLong(iTime);
        
        if(sys > max) {
          max = sys;
          maxTime = time;
        }
        if(sys < min) {
          min = sys;
          minTime = time;
        }
        sum += sys;
        trend = sys - prev;
        prev = sys;
        
        valSys.add(new Entry(sys, count));
        valDia.add(new Entry(cursor.getFloat(iDia), count));
        valMean.add(new Entry(cursor.getFloat(iMean), count));
        valHR.add(new Entry(cursor.getFloat(iHR), count));
        labels.add(timeFormat.format(new Date(time)));
        count++;
      }
  
      if(count > 1) {
        tvTrend.setText(trend > 0 ? "▲" : trend < 0 ? "▼" : "-");
      }
      if(count > 0) {
        tvAverage.setText(String.valueOf(sum / count));
        tvCount.setText(String.valueOf(count));
        tvMax.setText(String.valueOf(max));
        tvMaxTime.setText(timeFormat.format(new Date(maxTime)));
        tvMin.setText(String.valueOf(min));
        tvMinTime.setText(timeFormat.format(new Date(minTime)));
      }
      
      LineDataSet setSys = new LineDataSet(valSys, Application.getStringResource(R.string.bp_legend_systolic));
        setSys.setColor(COLOR_SYS);
        setSys.setCircleColor(COLOR_SYS);
      LineDataSet setDia = new LineDataSet(valDia, Application.getStringResource(R.string.bp_legend_diastolic));
        setDia.setColor(COLOR_DIA);
        setDia.setCircleColor(COLOR_DIA);
      LineDataSet setMean = new LineDataSet(valMean, Application.getStringResource(R.string.bp_legend_mean));
        setMean.setColor(COLOR_MEAN);
        setMean.setCircleColor(COLOR_MEAN);
      LineDataSet setRate = new LineDataSet(valHR, Application.getStringResource(R.string.bp_legend_heart_rate));
        setRate.setColor(COLOR_HR);
        setRate.setCircleColor(COLOR_HR);
  
      List<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setSys);
        dataSets.add(setDia);
        dataSets.add(setMean);
        dataSets.add(setRate);
  
      LineData values = new LineData(labels, dataSets);
      chart.setData(values);
      chart.invalidate();
    }
    catch (Exception e) {
      AndroidUtils.toast(getActivity(), "Failed to analyze data.");
      Log.e(TAG, "Failed to analyze data", e);
    }
    finally {
      CloseUtils.close(cursor);
    }
  }
  
  public void selectDateFrom(View view) {
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
      }
    });
    dialog.show(getFragmentManager(), "datePicker");
  }
  
  public void selectDateTo(View view) {
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
      }
    });
    dialog.show(getFragmentManager(), "datePicker");
  }
  
  private void refreshButtons() {
    bDateFrom.setText(dateFormat.format(dateFrom));
    bDateTo.setText(dateFormat.format(dateTo));
  }
  
  public void refreshAnalysis(View view) {
    if(loader != null) {
      loader.triggerLoad(dateFrom, dateTo);
    }
  }

  
  @Override
  public void onLoaderReset(Loader<AsyncTaskResult<Cursor>> result) { }
  
  @Override
  public Loader<AsyncTaskResult<Cursor>> onCreateLoader(int arg0, Bundle arg1) {
    loader = new BPMeasurementAnalysisLoader(getActivity(), userID);
    loader.setDates(dateFrom, dateTo);
    return loader;
  }
  
  
  public static Fragment newInstance(long userID) {
    Fragment instance = new BloodPressureAnalysisFragment();
      Bundle arguments = new Bundle(1);
        arguments.putLong(ARG_USER_ID, userID);
      instance.setArguments(arguments);
    return instance;
  }
}
