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
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.devices.blood_pressure.BloodPressureActivity;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateActivity;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.WeightScaleActivity;


public class DeviceListActivity extends ListActivity {
  private static final String TAG = DeviceListActivity.class.getSimpleName();
  
  public static final String EXTRA_USER_PROFILE = "extra.user.profile";
  
  
  private String[] itemNames = new String[]{
      "Krevní tlak (BT)",
      "Srdeční tep (ANT+)",
      "Váha (ANT+)"
  };
  
  @SuppressWarnings("unchecked")
  private Class<? extends Activity>[] itemActivities = (Class<? extends Activity>[]) new Class[]{
      BloodPressureActivity.class,
      HeartRateActivity.class,
      WeightScaleActivity.class
  };
  
  private Profile userProfile;
  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_list);
		
		userProfile = getIntent().getParcelableExtra(EXTRA_USER_PROFILE);
		
		if(userProfile == null){
		  Toast.makeText(this, R.string.alert_activity_not_launched_correctly, Toast.LENGTH_LONG).show();
		  Log.e(TAG, "User profile could not be retrieved from intent: extra name=" + EXTRA_USER_PROFILE);
		  finish();
		}
		
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemNames));
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	  Log.i(TAG, "List item selected: position=" + position + ", name=" + itemNames[position] + ", activity=" + itemActivities[position]);
	  
	  if(position >= 0 && position < itemActivities.length){
	    Intent deviceCommunicationIntent = new Intent(this, itemActivities[position]);
	    
	    //TODO asi předávat jinak?
	    deviceCommunicationIntent.putExtra(EXTRA_USER_PROFILE, userProfile);
	    
	    startActivity(deviceCommunicationIntent);
	  }
	  else {
	    super.onListItemClick(l, v, position, id);
	  }
	}
}
