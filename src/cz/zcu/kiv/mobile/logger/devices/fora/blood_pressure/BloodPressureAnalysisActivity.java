package cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure;

import cz.zcu.kiv.mobile.logger.data.types.NamedClass;
import cz.zcu.kiv.mobile.logger.devices.APagerActivity;


public class BloodPressureAnalysisActivity extends APagerActivity {

  @Override
  protected NamedClass[] getDataPages() {
    return new NamedClass[] {
        new NamedClass("Blood pressure", BloodPressureAnalysisFragment.class),
    };
  }
}
