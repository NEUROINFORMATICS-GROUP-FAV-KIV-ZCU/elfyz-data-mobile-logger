package cz.zcu.kiv.mobile.logger;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.database.Database;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.tables.ProfileTable;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.devices.DeviceListActivity;
import cz.zcu.kiv.mobile.logger.profiles.ProfileActivity;


public class MainActivity extends ListActivity {
  private static final String TAG = MainActivity.class.getSimpleName();
  
  private static final int REQUEST_CREATE_PROFILE = 1;
  
  private Database db;
  private SimpleCursorAdapter profileAdapter;

  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    db = ((Application) getApplication()).getDatabase();
    
    profileAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
        new String[]{ProfileTable.COLUMN_PROFILE_NAME}, new int[]{android.R.id.text1}, 0);
    setListAdapter(profileAdapter);
    
    registerForContextMenu(getListView());
  }
  
  @Override
  protected void onStart() {
    super.onStart();
    
    refreshProfileList();
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Log.d(TAG, "Profile selected: " + position);
    super.onListItemClick(l, v, position, id);
    
    Profile userProfile;
    try {
      userProfile = db.getProfile(id);
    }
    catch (DatabaseException e) {
      Toast.makeText(this, "Nepodaøilo se naèíst vybraný profil.", Toast.LENGTH_LONG).show();
      Log.e(TAG, "failed to retrieve profile: ID=" + id);
      return;
    }
    
    Intent intent = new Intent(this, DeviceListActivity.class);
    intent.putExtra(DeviceListActivity.EXTRA_USER_PROFILE, userProfile);
    startActivity(intent);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
  
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    getMenuInflater().inflate(R.menu.main_context, menu);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Log.d(TAG, "Menu item selected: " + item);
    switch (item.getItemId()) {
      case R.id.action_create_new_profile:
        createNewProfile(null);
        return true;
        
      default:
        return super.onOptionsItemSelected(item);
    }
  }
  
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    final long profileID = ((AdapterContextMenuInfo) item.getMenuInfo()).id;
    
    switch (item.getItemId()) {
      case R.id.context_delete:
        new AlertDialog.Builder(this)
        .setTitle(R.string.dialog_delete_profile_title)
        .setMessage(R.string.dialog_delete_profile_message)
        .setPositiveButton(R.string.dialog_delete_positive_button, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              try {
                db.deleteProfile(profileID);
              }
              catch (DatabaseException e) {
                Log.e(TAG, "Failed to delete profile: id=" + profileID, e);
                Toast.makeText(MainActivity.this, "Nepodaøilo se smazat profil. Naskytla se chyba pøi práci s databází.", Toast.LENGTH_LONG).show();
              }
              dialog.dismiss();
              refreshProfileList();
            }
          })
        .setNegativeButton(R.string.dialog_delete_cancel_button, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          })
        .show();
      return true;
  
      default:
        return super.onContextItemSelected(item);
    }
  }

  
  public void createNewProfile(View view){
    Log.d(TAG, "New profile creation requested.");
    startActivityForResult(new Intent(this, ProfileActivity.class), REQUEST_CREATE_PROFILE);
  }
  
  private void refreshProfileList() {
    Cursor profileCursor = null;
    try{
      profileCursor = db.getProfileNames();
    }
    catch(DatabaseException e){
      Log.e(TAG, "Failed to load profiles.", e);
      Toast.makeText(this, "Nepodaøilo se naèíst profily. Naskytla se chyba pøi práci s databází.", Toast.LENGTH_LONG).show();
      return;
    }
    
//    startManagingCursor(profileCursor); //TODO use LoaderManager and CursorLoader (and content providers?)
    profileAdapter.changeCursor(profileCursor);
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    
    if(requestCode == REQUEST_CREATE_PROFILE && resultCode == RESULT_OK){
      refreshProfileList();
    }
  }
  
  @Override
  protected void onDestroy() {
    db.close();

    super.onDestroy();
  }
}
