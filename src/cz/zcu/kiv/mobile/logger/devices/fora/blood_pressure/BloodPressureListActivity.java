package cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure;

import cz.zcu.kiv.mobile.logger.data.types.NamedClass;
import cz.zcu.kiv.mobile.logger.devices.ADataListActivity;


public class BloodPressureListActivity extends ADataListActivity {

  @Override
  protected NamedClass[] getDataPages() {
    return new NamedClass[] {
        new NamedClass("Measurements", BloodPressureListFragment.class),
    };
  }
}
