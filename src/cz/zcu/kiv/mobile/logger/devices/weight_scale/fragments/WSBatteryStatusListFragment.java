package cz.zcu.kiv.mobile.logger.devices.weight_scale.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.widget.CursorAdapter;
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.devices.ADataListFragment;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.adapters.WSBatteryStatusAdapter;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.loaders.WSBatteryStatusLoader;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.IExperimentParametersUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.WSBatteryStatusDbUploadHelper;


public class WSBatteryStatusListFragment extends ADataListFragment {
  private static final int LOADER_ID = 61;
  private static final String EXPERIMENT_PARAMETER_NAME = "WS Battery Status";
  
    
  @Override
  public Loader<AsyncTaskResult<Cursor>> onCreateLoader(int id, Bundle args) {
    return new WSBatteryStatusLoader(getActivity(), userID);
  }

  @Override
  protected int getLoaderID() {
    return LOADER_ID;
  }

  @Override
  protected IExperimentParametersUploadHelper getUploadHelper(long[] selected) {
    return new WSBatteryStatusDbUploadHelper(EXPERIMENT_PARAMETER_NAME, 0.0, selected, true);
  }

  @Override
  protected CursorAdapter getDataAdapter(Context context) {
    return new WSBatteryStatusAdapter(context, null, 0);
  }

  
  public static Fragment newInstance(long userID) {
    Fragment instance = new WSBatteryStatusListFragment();
      Bundle arguments = new Bundle(1);
        arguments.putLong(ARG_USER_ID, userID);
      instance.setArguments(arguments);
    return instance;
  }
}
