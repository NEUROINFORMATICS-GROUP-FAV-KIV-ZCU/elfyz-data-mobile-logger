package cz.zcu.kiv.mobile.logger.devices;

import java.lang.reflect.Method;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import cz.zcu.kiv.mobile.logger.data.types.NamedClass;


/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
 * of the sections/tabs/pages.
 */
public class DataListsPagerAdapter extends FragmentPagerAdapter {

  private NamedClass[] fragmentClasses;
  private long userID;

  
  public DataListsPagerAdapter(FragmentManager fm, long userID, NamedClass[] fragmentClasses) {
    super(fm);
    this.userID = userID;
    this.fragmentClasses = fragmentClasses;
  }

  
  @Override
  public Fragment getItem(int position) {
    if(position < 0 || position >= fragmentClasses.length)
      throw new RuntimeException("Unexpected position to instantiate fragment: " + position);
      
    try {
      Method m = fragmentClasses[position].clazz.getMethod("newInstance", Long.TYPE);
      return (Fragment) m.invoke(null, userID);
    }
    catch (NoSuchMethodException e) {
      throw new RuntimeException("Fragment class does not contain newInstance method with user profile parameter: position=" + position + ", fragment=" + fragmentClasses[position].clazz.getName(), e);
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to instantiate fragment: position=" + position + ", fragment=" + fragmentClasses[position].clazz.getName(), e);
    }
  }

  @Override
  public int getCount() {
    return fragmentClasses.length;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    if(position < 0 || position >= fragmentClasses.length)
      throw new RuntimeException("Unexpected position to instantiate fragment: " + position);
    
    return fragmentClasses[position].name;
  }
}