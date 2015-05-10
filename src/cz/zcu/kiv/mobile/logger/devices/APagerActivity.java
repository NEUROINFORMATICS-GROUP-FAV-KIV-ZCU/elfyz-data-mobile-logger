package cz.zcu.kiv.mobile.logger.devices;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.common.UserActivity;
import cz.zcu.kiv.mobile.logger.data.types.NamedClass;


public abstract class APagerActivity extends UserActivity {

  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide
   * fragments for each of the sections. We use a
   * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
   * keep every loaded fragment in memory. If this becomes too memory intensive,
   * it may be best to switch to a
   * {@link android.support.v4.app.FragmentStatePagerAdapter}.
   */
  private UserPagerAdapter mSectionsPagerAdapter;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  private ViewPager mViewPager;
  private View pagerTitle;

  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activities_pager);
    
    // Show the Up button in the action bar.
    getActionBar().setDisplayHomeAsUpEnabled(true);

    mViewPager = (ViewPager) findViewById(R.id.pager);
    pagerTitle = findViewById(R.id.pager_title_strip);
    
    // Create the adapter that will return a fragment for each of the three
    // primary sections of the app.
    mSectionsPagerAdapter = new UserPagerAdapter(getSupportFragmentManager(), userProfile.getId(), getDataPages());
    // Set up the ViewPager with the sections adapter.
    mViewPager.setAdapter(mSectionsPagerAdapter);
    
    if(mSectionsPagerAdapter.getCount() == 1) {
      pagerTitle.setVisibility(View.GONE);
    }
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
