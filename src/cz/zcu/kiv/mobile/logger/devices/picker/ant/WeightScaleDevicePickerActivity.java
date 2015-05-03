package cz.zcu.kiv.mobile.logger.devices.picker.ant;

import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController;


public class WeightScaleDevicePickerActivity extends AntDevicePickerActivity<AntPlusWeightScalePcc> {
  public static final String ACTION_DEVICE_SELECTED = "ant_plus.device.picker.weight_scale.selected";

  
  @Override
  protected AsyncScanController<AntPlusWeightScalePcc> requestAsyncScanController( int proximity) {
    return AntPlusWeightScalePcc.requestAsyncScanController(this, proximity, this);
  }

  @Override
  protected String getResultIntentAction() {
    return ACTION_DEVICE_SELECTED;
  }
}
