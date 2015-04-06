package cz.zcu.kiv.mobile.logger;

import java.util.List;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


public class SettingsActivity extends PreferenceActivity {

  @Override
  public void onBuildHeaders(List<Header> target) {
    loadHeadersFromResource(R.xml.preference_headers, target);
  }


  public static class ConnectionPrefsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      PreferenceManager.setDefaultValues(getActivity(), R.xml.connection_preferences, false);

      addPreferencesFromResource(R.xml.connection_preferences);
    }
  }

  public static class GeneralParametersPrefsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      PreferenceManager.setDefaultValues(getActivity(), R.xml.gen_pars_preferences, false);

      addPreferencesFromResource(R.xml.gen_pars_preferences);
    }
  }
}
