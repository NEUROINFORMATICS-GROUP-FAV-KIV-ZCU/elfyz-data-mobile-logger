package cz.zcu.kiv.mobile.logger.devices.weight_scale;

import cz.zcu.kiv.mobile.logger.data.types.NamedClass;
import cz.zcu.kiv.mobile.logger.devices.APagerActivity;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.fragments.WSBatteryStatusListFragment;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.fragments.WSManufacturerIdentificationListFragment;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.fragments.WSManufacturerSpecificListFragment;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.fragments.WSMeasurementListFragment;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.fragments.WSProductInformationListFragment;


public class WeightScaleDataListActivity extends APagerActivity {

  @Override
  protected NamedClass[] getDataPages() {
    return new NamedClass[] {
        new NamedClass("Measurements", WSMeasurementListFragment.class),
        new NamedClass("Product Information", WSProductInformationListFragment.class),
        new NamedClass("Manufacturer Identification", WSManufacturerIdentificationListFragment.class),
        new NamedClass("Battery Status", WSBatteryStatusListFragment.class),
        new NamedClass("Manufacturer Specific Data", WSManufacturerSpecificListFragment.class),
    };
  }
}
