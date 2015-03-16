package cz.zcu.kiv.mobile.logger.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import cz.zcu.kiv.mobile.logger.R;


public class ServiceManagementActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_service_management);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.service_management, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_close:
        stopService(new Intent(this, DeviceCommunicatorService.class)); //TODO finishes only if none is bound to it, force stop?
        return true;
  
      default: return super.onOptionsItemSelected(item);
    }
  }
}
