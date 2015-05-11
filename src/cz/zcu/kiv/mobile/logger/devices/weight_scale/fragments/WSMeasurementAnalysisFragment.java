package cz.zcu.kiv.mobile.logger.devices.weight_scale.fragments;

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
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.loaders.AAnalysisLoader;
import cz.zcu.kiv.mobile.logger.devices.AAnalysisFragment;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.loaders.WSMeasurementAnalysisLoader;


public class WSMeasurementAnalysisFragment extends AAnalysisFragment {
  private static final int LOADER_ID = 74;
  
  private static final int COLOR_WS = Color.MAGENTA;
  
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
    Fragment instance = new WSMeasurementAnalysisFragment();
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
    return new WSMeasurementAnalysisLoader(context, userID);
  }

  @Override
  protected int getLayoutResourceID() {
    return R.layout.fragment_analysis_ws;
  }

  @Override
  protected void analyze(Cursor cursor) throws Exception {
    List<Entry> valWs = new ArrayList<Entry>();
    List<String> labels = new ArrayList<String>();
    
    int iWs = cursor.getColumnIndex(WeightScaleMeasurementTable.COLUMN_WEIGHT);
    int iTime = cursor.getColumnIndex(WeightScaleMeasurementTable.COLUMN_TIME);
    
    int count = 0;
    double prev = 0;
    double trend = 0;
    double sum = 0;
    double max = Integer.MIN_VALUE;
    long maxTime = 0L;
    double min = Integer.MAX_VALUE;
    long minTime = 0L;
    
    while(cursor.moveToNext()) {
      double w = cursor.getDouble(iWs);
      long time = cursor.getLong(iTime);
      
      if(w > max) {
        max = w;
        maxTime = time;
      }
      if(w < min) {
        min = w;
        minTime = time;
      }
      sum += w;
      trend = w - prev;
      prev = w;
      
      valWs.add(new Entry((float) w, count));
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
    
    LineDataSet setWs = new LineDataSet(valWs, Application.getStringResource(R.string.ws_legend_weight));
      setWs.setColor(COLOR_WS);
      setWs.setCircleColor(COLOR_WS);

    List<LineDataSet> dataSets = new ArrayList<LineDataSet>();
      dataSets.add(setWs);

    LineData values = new LineData(labels, dataSets);
    chart.setData(values);
    chart.invalidate();
  }
}
