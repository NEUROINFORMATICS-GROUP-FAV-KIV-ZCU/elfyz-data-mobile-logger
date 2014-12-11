package cz.zcu.kiv.mobile.logger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.profiles.ProfileActivity;

//XXX just for developer testing
public class TestActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);

    System.out.println("\nSTARTING TEST ACTIVITY\n");
    startActivity(new Intent(this, ProfileActivity.class));

    finish();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.test, menu);
    return true;
  }

}
