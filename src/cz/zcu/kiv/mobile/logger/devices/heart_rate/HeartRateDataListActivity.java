package cz.zcu.kiv.mobile.logger.devices.heart_rate;

import cz.zcu.kiv.mobile.logger.data.types.NamedClass;
import cz.zcu.kiv.mobile.logger.devices.APagerActivity;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.fragments.HRCalculatedRrIntervalListFragment;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.fragments.HRCumulativeOperatingTimeListFragment;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.fragments.HRManufacturerAndSerialListFragment;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.fragments.HRMeasurementListFragment;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.fragments.HRPage4ListFragment;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.fragments.HRVersionAndModelListFragment;


public class HeartRateDataListActivity extends APagerActivity {

  @Override
  protected NamedClass[] getDataPages() {
    return new NamedClass[] {
        new NamedClass("Measurements", HRMeasurementListFragment.class),
        new NamedClass("RR Interval", HRCalculatedRrIntervalListFragment.class),
        new NamedClass("Version And Model", HRVersionAndModelListFragment.class),
        new NamedClass("Page 4", HRPage4ListFragment.class),
        new NamedClass("Manufacturer And Serial", HRManufacturerAndSerialListFragment.class),
        new NamedClass("Cumulative Operating Time", HRCumulativeOperatingTimeListFragment.class)
    };
  }
}
