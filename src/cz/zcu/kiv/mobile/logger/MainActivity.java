package cz.zcu.kiv.mobile.logger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
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
import cz.zcu.kiv.mobile.logger.data.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.data.database.ProfileTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.devices.DeviceListActivity;
import cz.zcu.kiv.mobile.logger.profiles.ProfileActivity;
import cz.zcu.kiv.mobile.logger.profiles.ProfileLoader;
import cz.zcu.kiv.mobile.logger.sync.AuthenticatorService;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public class MainActivity extends ListActivity implements LoaderCallbacks<AsyncTaskResult<Cursor>>{
  private static final String TAG = MainActivity.class.getSimpleName();

  private static final int LOADER_PROFILES = 1;
  
  private ProfileTable dbProfileTable;
  private SimpleCursorAdapter profileAdapter;

  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    dbProfileTable = Application.getInstance().getDatabase().getProfileTable();
    
    profileAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
        new String[]{ProfileTable.COLUMN_PROFILE_NAME}, new int[]{android.R.id.text1}, 0);
    setListAdapter(profileAdapter);
    
    registerForContextMenu(getListView());
    
    getLoaderManager().initLoader(LOADER_PROFILES, null, this);
    
    setupSyncAccount();
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Log.d(TAG, "Profile selected: " + position);
    super.onListItemClick(l, v, position, id);
    
    Profile userProfile;
    try {
      userProfile = dbProfileTable.getProfile(id);
    }
    catch (DatabaseException e) {
      AndroidUtils.toast(this, R.string.profile_load_fail);
      Log.e(TAG, "failed to retrieve profile: ID=" + id);
      return;
    }
    
    Application.getInstance().setUserProfile(userProfile);
    
    //TODO data syncing setting
    if(userProfile.getEegbasePassword() != null) {
      Account account = new Account(String.valueOf(userProfile.getId()), AuthenticatorService.ACCOUNT_TYPE);
      ContentResolver.setSyncAutomatically(account, AuthenticatorService.AUTHORITY, true);
    }
    
    startActivity(new Intent(this, DeviceListActivity.class));
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
        
      case R.id.action_settings:
        startActivity(new Intent(this, SettingsActivity.class));
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
                removeProfile(profileID);
                dialog.dismiss();
              }
            })
          .setNegativeButton(R.string.dialog_cancel_button, new OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            })
          .show();
        return true;
        
      case R.id.context_edit:
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_PROFILE_ID, profileID);
        startActivity(intent);
        return true;
  
      default:
        return super.onContextItemSelected(item);
    }
  }

  
  public void createNewProfile(View view){
    Log.d(TAG, "New profile creation requested.");
    startActivity(new Intent(this, ProfileActivity.class));
  }
  
  private void setupSyncAccount() {
    // Create the account type and default account
    Account newAccount = new Account("setup_account", AuthenticatorService.ACCOUNT_TYPE);
    // Get an instance of the Android account manager
    AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
    /*
     * Add the account and account type, no password or user data
     * If successful, return the Account object, otherwise report an error.
     */
    if ( !accountManager.addAccountExplicitly(newAccount, null, null)) {
      Log.w(TAG, "sync account exists or error occured while setup");
    }
  }

  private void removeProfile(long profileID) {
    try {
      dbProfileTable.deleteProfile(profileID);
    }
    catch (DatabaseException e) {
      Log.e(TAG, "Failed to delete profile: id=" + profileID, e);
      AndroidUtils.toast(MainActivity.this, R.string.profile_delete_fail);
      return;
    }

    Account removeAccount = new Account(String.valueOf(profileID), AuthenticatorService.ACCOUNT_TYPE);
    AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
    Account[] a1 = accountManager.getAccounts();  //TODO
    accountManager.removeAccount(removeAccount, null, null);
    Account[] a2 = accountManager.getAccounts();
  }
  
  
  @Override
  public Loader<AsyncTaskResult<Cursor>> onCreateLoader(int id, Bundle args) {
    return new ProfileLoader(this);
  }

  @Override
  public void onLoadFinished(Loader<AsyncTaskResult<Cursor>> loader, AsyncTaskResult<Cursor> data) {
    if(loader.getId() == LOADER_PROFILES) {
      if(data.getError() != null) {
        AndroidUtils.toast(this, R.string.fail_load_profiles);
        Log.e(TAG, "Failed to load profiles.", data.getError());
      }
      else {
        profileAdapter.swapCursor(data.getResult());
      }
    }
  }

  @Override
  public void onLoaderReset(Loader<AsyncTaskResult<Cursor>> data) {
    profileAdapter.swapCursor(null); 
  }
}
