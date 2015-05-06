package cz.zcu.kiv.mobile.logger.devices.heart_rate.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.widget.CursorAdapter;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.ARecordTable;
import cz.zcu.kiv.mobile.logger.data.types.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.devices.ADataListFragment;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.adapters.HRCumulativeOperatingTimeAdapter;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.loaders.HRCumulativeOperatingTimeLoader;
import cz.zcu.kiv.mobile.logger.eegbase.upload.helpers.HRCumulativeOperatingTimeDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload.helpers.IExperimentParametersUploadHelper;


public class HRCumulativeOperatingTimeListFragment extends ADataListFragment {
  private static final int LOADER_ID = 52;
  
  
  @Override
  public Loader<AsyncTaskResult<Cursor>> onCreateLoader(int id, Bundle args) {
    return new HRCumulativeOperatingTimeLoader(getActivity(), userID);
  }

  @Override
  protected int getLoaderID() {
    return LOADER_ID;
  }

  @Override
  protected ARecordTable<?> getDatabase() {
    return Application.getInstance().getDatabase().getHeartRateCumulativeOperatingTimeTable();
  }

  @Override
  protected IExperimentParametersUploadHelper getUploadHelper(long[] selected) {
    String parameterName = Application.getPreferences().getString("pref_gen_par_name_hr_copt", "HR Cumulative Operating Time");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    return new HRCumulativeOperatingTimeDbUploadHelper(parameterName, 0.0, selected, append);
  }

  @Override
  protected CursorAdapter getDataAdapter(Context context) {
    return new HRCumulativeOperatingTimeAdapter(context, null, 0);
  }

  
  public static Fragment newInstance(long userID) {
    Fragment instance = new HRCumulativeOperatingTimeListFragment();
      Bundle arguments = new Bundle(1);
        arguments.putLong(ARG_USER_ID, userID);
      instance.setArguments(arguments);
    return instance;
  }
}
