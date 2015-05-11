package cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure;

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
import cz.zcu.kiv.mobile.logger.data.database.BloodPressureMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.loaders.AAnalysisLoader;
import cz.zcu.kiv.mobile.logger.devices.AAnalysisFragment;


public class BloodPressureAnalysisFragment extends AAnalysisFragment {
  private static final int LOADER_ID = 71;
  
  private static final int COLOR_LIMIT_HIGH = Color.RED;
  private static final int COLOR_LIMIT_PREHIGH = Color.YELLOW;
  private static final int COLOR_LIMIT_OPTIMAL = Color.GREEN;
  private static final int COLOR_LIMIT_LOW = Color.DKGRAY;
  private static final int COLOR_SYS = Color.CYAN;
  private static final int COLOR_DIA = Color.BLUE;
  private static final int COLOR_MEAN = Color.LTGRAY;
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

  
  public static Fragment newInstance(long userID) {
    Fragment instance = new BloodPressureAnalysisFragment();
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
    return new BPMeasurementAnalysisLoader(context, userID);
  }

  @Override
  protected int getLayoutResourceID() {
    return R.layout.fragment_analysis_bp;
  }

  @Override
  protected void analyze(Cursor cursor) throws Exception {
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
}
