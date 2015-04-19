package cz.zcu.kiv.mobile.logger.devices.fora.glucose;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.types.NamedClass;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.devices.DataListsPagerAdapter;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public class GlucoseMeterListActivity extends FragmentActivity {
  private static final String TAG = GlucoseMeterListActivity.class.getSimpleName();
  

  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide
   * fragments for each of the sections. We use a
   * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
   * keep every loaded fragment in memory. If this becomes too memory intensive,
   * it may be best to switch to a
   * {@link android.support.v4.app.FragmentStatePagerAdapter}.
   */
  DataListsPagerAdapter mSectionsPagerAdapter;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  ViewPager mViewPager;
  
  private Profile userProfile;
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activities_data_list);
    
    userProfile = Application.getInstance().getUserProfileOrLogIn();
    
    if(userProfile == null) {
      AndroidUtils.toast(this, R.string.alert_must_be_logged_in);
      Log.e(TAG, "User must be logged in.");
      finish();
      return;
    }

    // Show the Up button in the action bar.
    getActionBar().setDisplayHomeAsUpEnabled(true);

    // Create the adapter that will return a fragment for each of the three
    // primary sections of the app.
    mSectionsPagerAdapter = new DataListsPagerAdapter(getSupportFragmentManager(), userProfile.getId(),
        new NamedClass[] {
          new NamedClass("Measurements", GlucoseMeterListFragment.class),
      });

    // Set up the ViewPager with the sections adapter.
    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mSectionsPagerAdapter);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
      }
    return super.onOptionsItemSelected(item);
  }
}
