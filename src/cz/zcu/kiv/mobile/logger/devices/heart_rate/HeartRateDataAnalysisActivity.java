package cz.zcu.kiv.mobile.logger.devices.heart_rate;

import cz.zcu.kiv.mobile.logger.data.types.NamedClass;
import cz.zcu.kiv.mobile.logger.devices.APagerActivity;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.fragments.HRMeasurementAnalysisFragment;


public class HeartRateDataAnalysisActivity extends APagerActivity {

  @Override
  protected NamedClass[] getDataPages() {
    return new NamedClass[] {
        new NamedClass("Heart rate", HRMeasurementAnalysisFragment.class),
    };
  }
}
