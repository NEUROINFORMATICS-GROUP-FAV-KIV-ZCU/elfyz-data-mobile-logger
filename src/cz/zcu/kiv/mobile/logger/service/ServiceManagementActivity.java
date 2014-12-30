package cz.zcu.kiv.mobile.logger.service;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
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

}
