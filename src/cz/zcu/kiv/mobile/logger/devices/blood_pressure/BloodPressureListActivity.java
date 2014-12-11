package cz.zcu.kiv.mobile.logger.devices.blood_pressure;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cz.zcu.kiv.mobile.logger.R;


public class BloodPressureListActivity extends ListActivity {
  public static final String EXTRA_MEASUREMENTS = "extra_measurements";
  private static final String TAG = BloodPressureListActivity.class.getSimpleName();
  private static final String STATE_MEASUREMENTS = "state_measurements";

  private ArrayList<BloodPressureMeasurement> measurements = new ArrayList<BloodPressureMeasurement>();
  private static SimpleDateFormat timeFormat;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_blood_pressure_list);
    
    ArrayList<BloodPressureMeasurement> extra = getIntent().getParcelableArrayListExtra(EXTRA_MEASUREMENTS);
    if(extra == null) {
      Log.w(TAG, "No measurements received in intent.");
    }
    else {
      measurements = extra;
    }

    timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    setListAdapter(new CustomListAdapter(this, android.R.layout.simple_list_item_1, measurements));
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.blood_pressure_list, menu);
    return true;
  }
  
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    
    outState.putParcelableArrayList(STATE_MEASUREMENTS, measurements);
  }
  
  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    
    measurements = savedInstanceState.getParcelableArrayList(STATE_MEASUREMENTS);
  }
  
  
  
  private static class CustomListAdapter extends ArrayAdapter<BloodPressureMeasurement> {
    
    public CustomListAdapter(Context context, int resource, List<BloodPressureMeasurement> values) {
      super(context, resource, values);
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if(convertView == null){
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.blood_pressure_row, null);
      }
      
      BloodPressureMeasurement measurement = getItem(position);
      
      if(measurement != null){
        TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
        TextView tvSystolic = (TextView) convertView.findViewById(R.id.tv_systolic);
        TextView tvDiastolic = (TextView) convertView.findViewById(R.id.tv_diastolic);
        TextView tvMeanPressure = (TextView) convertView.findViewById(R.id.tv_mean_pressure);
        TextView tvHeartRate = (TextView) convertView.findViewById(R.id.tv_heart_rate);
        
        tvTime.setText(timeFormat.format(measurement.getTime().getTime()));
        tvSystolic.setText("SYS " + String.valueOf(measurement.getSystolicPressure()));
        tvDiastolic.setText("DIA " + String.valueOf(measurement.getDiastolicPressure()));
        tvMeanPressure.setText("Ø " + String.valueOf(measurement.getMeanPressure()));
        tvHeartRate.setText("♥ " + String.valueOf(measurement.getHeartRate()));
      }

      return convertView;
    }
  }
}
