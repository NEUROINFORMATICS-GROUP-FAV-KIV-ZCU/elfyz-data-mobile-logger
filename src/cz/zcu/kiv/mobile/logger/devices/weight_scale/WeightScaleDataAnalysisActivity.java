package cz.zcu.kiv.mobile.logger.devices.weight_scale;

import cz.zcu.kiv.mobile.logger.data.types.NamedClass;
import cz.zcu.kiv.mobile.logger.devices.APagerActivity;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.fragments.WSMeasurementAnalysisFragment;


public class WeightScaleDataAnalysisActivity extends APagerActivity {

  @Override
  protected NamedClass[] getDataPages() {
    return new NamedClass[] {
        new NamedClass("Weight", WSMeasurementAnalysisFragment.class),
    };
  }
}
