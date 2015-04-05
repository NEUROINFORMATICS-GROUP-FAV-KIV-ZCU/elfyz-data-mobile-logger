package cz.zcu.kiv.mobile.logger.devices.heart_rate.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.widget.CursorAdapter;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.devices.ADataListFragment;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.adapters.HRManufacturerAndSerialAdapter;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.loaders.HRManufacturerAndSerialLoader;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.HRManufacturerAndSerialDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.IExperimentParametersUploadHelper;


public class HRManufacturerAndSerialListFragment extends ADataListFragment {
  private static final int LOADER_ID = 53;
  private static final String EXPERIMENT_PARAMETER_NAME = "HR Manufacturer And Serial";
  

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
    return new HRManufacturerAndSerialDbUploadHelper(EXPERIMENT_PARAMETER_NAME, 0.0, selected, false);
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
