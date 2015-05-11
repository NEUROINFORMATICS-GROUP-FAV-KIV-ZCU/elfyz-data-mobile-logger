package cz.zcu.kiv.mobile.logger.devices.fora.glucose;

import cz.zcu.kiv.mobile.logger.data.types.NamedClass;
import cz.zcu.kiv.mobile.logger.devices.APagerActivity;


public class GlucoseMeterAnalysisActivity extends APagerActivity {

  @Override
  protected NamedClass[] getDataPages() {
    return new NamedClass[] {
        new NamedClass("Glucose", GlucoseMeterAnalysisFragment.class),
    };
  }
}
