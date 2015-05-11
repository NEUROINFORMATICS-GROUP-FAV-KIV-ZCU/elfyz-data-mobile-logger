package cz.zcu.kiv.mobile.logger.devices.fora.glucose;

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
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.database.GlucoseMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.loaders.AAnalysisLoader;
import cz.zcu.kiv.mobile.logger.devices.AAnalysisFragment;


public class GlucoseMeterAnalysisFragment extends AAnalysisFragment {
  private static final int LOADER_ID = 72;

  private static final int COLOR_LIMIT_HIGH = Color.RED;
  private static final int COLOR_LIMIT_OPTIMAL = Color.GREEN;
  private static final int COLOR_GM = Color.CYAN;

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

    YAxis leftAxis = chart.getAxisLeft();
    leftAxis.setDrawLimitLinesBehindData(true);
    LimitLine ll = new LimitLine(110f, Application.getStringResource(R.string.gm_level_high));
    ll.setLineColor(COLOR_LIMIT_HIGH);
    ll.setLineWidth(4f);
    ll.setTextColor(COLOR_LIMIT_HIGH);
    ll.setTextSize(10f);
    ll.setLabelPosition(LimitLabelPosition.POS_RIGHT);
    leftAxis.addLimitLine(ll);
    ll = new LimitLine(72f, Application.getStringResource(R.string.gm_level_optimal));
    ll.setLineColor(COLOR_LIMIT_OPTIMAL);
    ll.setLineWidth(4f);
    ll.setTextColor(COLOR_LIMIT_OPTIMAL);
    ll.setTextSize(10f);
    ll.setLabelPosition(LimitLabelPosition.POS_RIGHT);
    leftAxis.addLimitLine(ll);
  }

  @Override
  protected int getLoaderID() {
    return LOADER_ID;
  }
  
  @Override
  protected int getLayoutResourceID() {
    return R.layout.fragment_analysis_gm;
  }

  @Override
  protected AAnalysisLoader getAnalysisLoader(Context context, long userID) {
    return new GMeasurementAnalysisLoader(context, userID);
  }

  @Override
  protected void analyze(Cursor cursor) throws Exception {
    List<Entry> valGl = new ArrayList<Entry>();
    List<String> labels = new ArrayList<String>();

    int iGl = cursor.getColumnIndex(GlucoseMeasurementTable.COLUMN_GLUCOSE);
    int iTime = cursor.getColumnIndex(GlucoseMeasurementTable.COLUMN_TIME);

    int count = 0;
    int prev = 0;
    int trend = 0;
    int sum = 0;
    int max = Integer.MIN_VALUE;
    long maxTime = 0L;
    int min = Integer.MAX_VALUE;
    long minTime = 0L;

    while(cursor.moveToNext()) {
      int gl = cursor.getInt(iGl);
      long time = cursor.getLong(iTime);

      if(gl > max) {
        max = gl;
        maxTime = time;
      }
      if(gl < min) {
        min = gl;
        minTime = time;
      }
      sum += gl;
      trend = gl - prev;
      prev = gl;

      valGl.add(new Entry(gl, count));
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

    LineDataSet setGl = new LineDataSet(valGl, Application.getStringResource(R.string.gm_legend_glucose));
      setGl.setColor(COLOR_GM);
      setGl.setCircleColor(COLOR_GM);

    List<LineDataSet> dataSets = new ArrayList<LineDataSet>();
      dataSets.add(setGl);

    LineData values = new LineData(labels, dataSets);
    chart.setData(values);
    chart.invalidate();
  }

  
  public static Fragment newInstance(long userID) {
    Fragment instance = new GlucoseMeterAnalysisFragment();
      Bundle arguments = new Bundle(1);
        arguments.putLong(ARG_USER_ID, userID);
      instance.setArguments(arguments);
    return instance;
  }
}

