package cz.zcu.kiv.mobile.logger.ant.picker;

import java.util.HashSet;
import java.util.Set;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.AsyncScanResultDeviceInfo;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.IAsyncScanResultReceiver;

import cz.zcu.kiv.mobile.logger.R;


public abstract class AntDevicePickerActivity<T extends AntPluginPcc> extends ListActivity implements IAsyncScanResultReceiver {
  public static final String EXTRA_DEVICE = "picked.ant.plus.device";
  
  protected TextView vEmpty;
  protected ProgressBar vProgress;
  protected Button bSearch;
  
  protected AsyncScanController<T> controller;
  protected AntDeviceAdapter deviceAdapter;
  
  protected Intent result;
  
  private Set<Integer> foundDevices;
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ant_device_picker);
    
    vEmpty = (TextView) findViewById(android.R.id.empty);
    vProgress = (ProgressBar) findViewById(R.id.progress);
    bSearch = (Button) findViewById(R.id.b_search);
    
    deviceAdapter = new AntDeviceAdapter(this);
    setListAdapter(deviceAdapter);
    
    result = new Intent(getResultIntentAction());

    foundDevices = new HashSet<Integer>();
    
    startSearch(null);
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    
    cancelSearch(null);
    deviceAdapter.clear();
    foundDevices.clear();
    
    LocalBroadcastManager.getInstance(this).sendBroadcast(result);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.ant_device_picker, menu);
    return true;
  }
  

  @Override
  public void onSearchResult(AsyncScanResultDeviceInfo device) {
    if( !foundDevices.contains(device.getAntDeviceNumber()) ) {
      foundDevices.add(device.getAntDeviceNumber());
      deviceAdapter.add(device);
    }
  }

  @Override
  public void onSearchStopped(RequestAccessResult result) {
    bSearch.setVisibility(View.VISIBLE);
    vProgress.setVisibility(View.GONE);
    vEmpty.setText("Nebyla nalezena žádná zařízení.");
    controller = null;
  }
  
  protected void onSearchStarted() {
    bSearch.setVisibility(View.GONE);
    vProgress.setVisibility(View.VISIBLE);
    vEmpty.setText("Hledají se zařízení...");
  }

  public void startSearch(View view) {
    controller = requestAsyncScanController(0);
    
    deviceAdapter.clear();
    foundDevices.clear();
    onSearchStarted();
  }

  public void cancelSearch(View view) {
    if (controller != null) {
      controller.closeScanController();
      controller = null;
    }
  }
  
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    cancelSearch(v);
    
    AsyncScanResultDeviceInfo device = deviceAdapter.getItem(position);
    result.putExtra(EXTRA_DEVICE, device);
    
    finish();
  }

  protected abstract AsyncScanController<T> requestAsyncScanController(int proximity);
  protected abstract String getResultIntentAction();
}
