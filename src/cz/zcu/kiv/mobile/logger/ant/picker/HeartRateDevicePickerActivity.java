package cz.zcu.kiv.mobile.logger.ant.picker;

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController;


public class HeartRateDevicePickerActivity extends AntDevicePickerActivity<AntPlusHeartRatePcc> {
  public static final String ACTION_DEVICE_SELECTED = "ant_plus.device.picker.heart_rate.selected";
  

  @Override
  protected AsyncScanController<AntPlusHeartRatePcc> requestAsyncScanController( int proximity) {
    return AntPlusHeartRatePcc.requestAsyncScanController(this, proximity, this);
  }

  @Override
  protected String getResultIntentAction() {
    return ACTION_DEVICE_SELECTED;
  }
}
