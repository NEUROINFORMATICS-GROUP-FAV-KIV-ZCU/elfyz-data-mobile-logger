package cz.zcu.kiv.mobile.logger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import cz.zcu.kiv.mobile.logger.data.database.Database;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateActivity;

//XXX just for developer testing
public class TestActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);

    Database db = ((Application) getApplication()).getDatabase();
    
    Intent testIntent = new Intent(this, HeartRateActivity.class);
    try {
      Application.getInstance().setUserProfile(db.getProfileTable().getProfile(1L));
    
      System.out.println("\nSTARTING TEST ACTIVITY\n");
      startActivity(testIntent);
  
      finish();
    }
    catch (DatabaseException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.test, menu);
    return true;
  }

}
