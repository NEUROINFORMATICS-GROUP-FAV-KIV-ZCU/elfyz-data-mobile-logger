package cz.zcu.kiv.mobile.logger.devices;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure.BloodPressureActivity;
import cz.zcu.kiv.mobile.logger.devices.fora.glucose.GlucoseMeterActivity;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateActivity;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.WeightScaleActivity;


public class DeviceListActivity extends ListActivity {
  private static final String TAG = DeviceListActivity.class.getSimpleName();
  
  
  private String[] deviceNames;
  private Class<? extends Activity>[] deviceActivities;
  private Profile userProfile;
  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_list);
		
		userProfile = Application.getInstance().getUserProfileOrLogIn();
		
		if(userProfile == null){
		  Toast.makeText(this, R.string.alert_must_be_logged_in, Toast.LENGTH_LONG).show();
		  Log.e(TAG, "User must be logged in.");
		  finish();
		}
		
		prepareArrays();
		
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceNames));
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	  Log.i(TAG, "List item selected: position=" + position + ", name=" + deviceNames[position] + ", activity=" + deviceActivities[position]);
	  
	  if(position >= 0 && position < deviceActivities.length){
	    startActivity(new Intent(this, deviceActivities[position]));
	  }
	  else {
	    super.onListItemClick(l, v, position, id);
	  }
	}
	
	
	@SuppressWarnings("unchecked")
  private void prepareArrays() {
    deviceNames = new String[] {
	      getString(R.string.device_list_blood_pressure),
	      getString(R.string.device_list_heart_rate),
	      getString(R.string.device_list_weight_scale),
	      getString(R.string.device_list_glucose_meter)
	  };
    deviceActivities = (Class<? extends Activity>[]) new Class[] {
        BloodPressureActivity.class,
        HeartRateActivity.class,
        WeightScaleActivity.class,
        GlucoseMeterActivity.class
    };
	}
}
