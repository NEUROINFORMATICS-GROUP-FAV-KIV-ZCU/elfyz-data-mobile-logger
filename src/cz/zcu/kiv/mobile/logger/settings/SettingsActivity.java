package cz.zcu.kiv.mobile.logger.settings;

import java.util.List;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import cz.zcu.kiv.mobile.logger.R;


public class SettingsActivity extends PreferenceActivity {

  @Override
  public void onBuildHeaders(List<Header> target) {
    loadHeadersFromResource(R.xml.preference_headers, target);
  }


  public static class ConnectionPrefsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences_connection, false);

      addPreferencesFromResource(R.xml.preferences_connection);
    }
  }

  public static class GeneralParametersPrefsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences_gen_pars, false);

      addPreferencesFromResource(R.xml.preferences_gen_pars);
    }
  }
}
