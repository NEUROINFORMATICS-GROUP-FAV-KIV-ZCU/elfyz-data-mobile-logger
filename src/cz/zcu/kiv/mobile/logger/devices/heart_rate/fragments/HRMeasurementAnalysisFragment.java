package cz.zcu.kiv.mobile.logger.devices.heart_rate.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.loaders.AAnalysisLoader;
import cz.zcu.kiv.mobile.logger.devices.AAnalysisFragment;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.loaders.HRMeasurementAnalysisLoader;


public class HRMeasurementAnalysisFragment extends AAnalysisFragment {
  private static final int LOADER_ID = 73;
  
  private static final int COLOR_HR = Color.MAGENTA;
  
  private LineChart chart;
  
  private TextView tvTrend;
  private TextView tvAverage;
  private TextView tvCount;
  private TextView tvMax;
  private TextView tvMaxTime;
  private TextView tvMin;
  private TextView tvMinTime;

  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = super.onCreateView(inflater, container, savedInstanceState);
    chart = (LineChart) rootView.findViewById(R.id.chart);
    tvTrend = (TextView) rootView.findViewById(R.id.tv_stats_trend);
    tvAverage = (TextView) rootView.findViewById(R.id.tv_stats_average);
    tvCount = (TextView) rootView.findViewById(R.id.tv_stats_count);
    tvMax = (TextView) rootView.findViewById(R.id.tv_stats_max);
    tvMaxTime = (TextView) rootView.findViewById(R.id.tv_stats_max_time);
    tvMin = (TextView) rootView.findViewById(R.id.tv_stats_min);
    tvMinTime = (TextView) rootView.findViewById(R.id.tv_stats_min_time);
    initChart();
    return rootView;
  }
  
  private void initChart() {
    chart.setNoDataText(Application.getStringResource(R.string.no_data));
    chart.setDragEnabled(true);
    chart.setDrawGridBackground(true);
  }

  
  public static Fragment newInstance(long userID) {
    Fragment instance = new HRMeasurementAnalysisFragment();
      Bundle arguments = new Bundle(1);
        arguments.putLong(ARG_USER_ID, userID);
      instance.setArguments(arguments);
    return instance;
  }


  @Override
  protected int getLoaderID() {
    return LOADER_ID;
  }

  @Override
  protected AAnalysisLoader getAnalysisLoader(Context context, long userID) {
    return new HRMeasurementAnalysisLoader(context, userID);
  }

  @Override
  protected int getLayoutResourceID() {
    return R.layout.fragment_analysis_hr;
  }

  @Override
  protected void analyze(Cursor cursor) throws Exception {
    List<Entry> valHR = new ArrayList<Entry>();
    List<String> labels = new ArrayList<String>();
    
    int iHR = cursor.getColumnIndex(HeartRateMeasurementTable.COLUMN_HEART_RATE);
    int iTime = cursor.getColumnIndex(HeartRateMeasurementTable.COLUMN_TIME);
    
    int count = 0;
    int prev = 0;
    int trend = 0;
    int sum = 0;
    int max = Integer.MIN_VALUE;
    long maxTime = 0L;
    int min = Integer.MAX_VALUE;
    long minTime = 0L;
    
    while(cursor.moveToNext()) {
      int hr = cursor.getInt(iHR);
      long time = cursor.getLong(iTime);
      
      if(hr > max) {
        max = hr;
        maxTime = time;
      }
      if(hr < min) {
        min = hr;
        minTime = time;
      }
      sum += hr;
      trend = hr - prev;
      prev = hr;
      
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
    
    LineDataSet setHR = new LineDataSet(valHR, Application.getStringResource(R.string.hr_legend_heart_rate));
      setHR.setColor(COLOR_HR);
      setHR.setCircleColor(COLOR_HR);

    List<LineDataSet> dataSets = new ArrayList<LineDataSet>();
      dataSets.add(setHR);

    LineData values = new LineData(labels, dataSets);
    chart.setData(values);
    chart.invalidate();
  }
}
