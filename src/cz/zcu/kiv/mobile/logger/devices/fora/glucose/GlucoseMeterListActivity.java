package cz.zcu.kiv.mobile.logger.devices.fora.glucose;

import cz.zcu.kiv.mobile.logger.data.types.NamedClass;
import cz.zcu.kiv.mobile.logger.devices.ADataListActivity;


public class GlucoseMeterListActivity extends ADataListActivity {
  
  @Override
  protected NamedClass[] getDataPages() {
    return new NamedClass[] {
        new NamedClass("Measurements", GlucoseMeterListFragment.class),
    };
  }
}
