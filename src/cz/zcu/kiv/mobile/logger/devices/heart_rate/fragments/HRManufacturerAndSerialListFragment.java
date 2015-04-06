package cz.zcu.kiv.mobile.logger.devices.heart_rate.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.widget.CursorAdapter;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.devices.ADataListFragment;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.adapters.HRManufacturerAndSerialAdapter;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.loaders.HRManufacturerAndSerialLoader;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.HRManufacturerAndSerialDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.IExperimentParametersUploadHelper;


public class HRManufacturerAndSerialListFragment extends ADataListFragment {
  private static final int LOADER_ID = 53;
  

  @Override
  public Loader<AsyncTaskResult<Cursor>> onCreateLoader(int id, Bundle args) {
    return new HRManufacturerAndSerialLoader(getActivity(), userID);
  }

  @Override
  protected int getLoaderID() {
    return LOADER_ID;
  }

  @Override
  protected IExperimentParametersUploadHelper getUploadHelper(long[] selected) {
    String parameterName = Application.getPreferences().getString("pref_gen_par_name_hr_mas", "HR Manufacturer And Serial");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    return new HRManufacturerAndSerialDbUploadHelper(parameterName, 0.0, selected, append);
  }

  @Override
  protected CursorAdapter getDataAdapter(Context context) {
    return new HRManufacturerAndSerialAdapter(getActivity(), null, 0);
  }

  
  public static Fragment newInstance(long userID) {
    Fragment instance = new HRManufacturerAndSerialListFragment();
      Bundle arguments = new Bundle(1);
        arguments.putLong(ARG_USER_ID, userID);
      instance.setArguments(arguments);
    return instance;
  }
}
