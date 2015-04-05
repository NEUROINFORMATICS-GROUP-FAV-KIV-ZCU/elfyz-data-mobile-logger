package cz.zcu.kiv.mobile.logger.devices.heart_rate.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.widget.CursorAdapter;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.devices.ADataListFragment;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.adapters.HRMeasurementAdapter;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.loaders.HRMeasurementLoader;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.HRMeasurementDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.IExperimentParametersUploadHelper;


public class HRMeasurementListFragment extends ADataListFragment {
  private static final int LOADER_ID = 54;
  private static final String EXPERIMENT_PARAMETER_NAME = "HR Measurement";

  
  @Override
  public Loader<AsyncTaskResult<Cursor>> onCreateLoader(int id, Bundle args) {
    return new HRMeasurementLoader(getActivity(), userID);
  }

  @Override
  protected int getLoaderID() {
    return LOADER_ID;
  }

  @Override
  protected IExperimentParametersUploadHelper getUploadHelper(long[] selected) {
    return new HRMeasurementDbUploadHelper(EXPERIMENT_PARAMETER_NAME, 0.0, selected, false);
  }

  @Override
  protected CursorAdapter getDataAdapter(Context context) {
    return new HRMeasurementAdapter(context, null, 0);
  }

  
  public static Fragment newInstance(long userID) {
    Fragment instance = new HRMeasurementListFragment();
      Bundle arguments = new Bundle(1);
        arguments.putLong(ARG_USER_ID, userID);
      instance.setArguments(arguments);
    return instance;
  }
}
