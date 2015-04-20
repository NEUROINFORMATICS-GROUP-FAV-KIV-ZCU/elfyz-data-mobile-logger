package cz.zcu.kiv.mobile.logger.devices;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.common.UserActivity;
import cz.zcu.kiv.mobile.logger.data.types.NamedClass;


public abstract class ADataListActivity extends UserActivity {

  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide
   * fragments for each of the sections. We use a
   * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
   * keep every loaded fragment in memory. If this becomes too memory intensive,
   * it may be best to switch to a
   * {@link android.support.v4.app.FragmentStatePagerAdapter}.
   */
  private DataListsPagerAdapter mSectionsPagerAdapter;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  private ViewPager mViewPager;

  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activities_data_list);
    
    // Show the Up button in the action bar.
    getActionBar().setDisplayHomeAsUpEnabled(true);

    // Create the adapter that will return a fragment for each of the three
    // primary sections of the app.
    mSectionsPagerAdapter = new DataListsPagerAdapter(getSupportFragmentManager(), userProfile.getId(), getDataPages());

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

  
  protected abstract NamedClass[] getDataPages();
}