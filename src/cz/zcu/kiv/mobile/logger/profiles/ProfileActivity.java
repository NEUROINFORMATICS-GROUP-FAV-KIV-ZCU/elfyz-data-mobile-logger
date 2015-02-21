package cz.zcu.kiv.mobile.logger.profiles;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.database.ProfileTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DuplicateEntryException;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.EntryNotFoundException;
import cz.zcu.kiv.mobile.logger.data.types.Gender;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.login.LoginActivity;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;
import cz.zcu.kiv.mobile.logger.utils.DateUtils;


public class ProfileActivity extends Activity implements OnDateSetListener {
  private static final String TAG = ProfileActivity.class.getSimpleName();
  
  public static final String EXTRA_PROFILE_ID = "extra.profile.id";
  
  private static final String STATE_SELECTED_DATE = "state.selected.date";
  private static final String STATE_PROFILE = "extra.profile";
  private static final int REQUEST_LOGIN = 5;
  
  private EditText vProfileName;
  private TextView vEmail;
  private TextView vName;
  private TextView vSurname;
  private TextView vBirthDate;
  private Spinner vGender;
  private EditText vHeight;
  private RadioGroup vActivityLevel;
  private CheckBox vLifetimeAthlete;

  private DateFormat dateFormat;
  
  private ProfileTable dbProfile;
  private Calendar selectedDate;
  
  private Profile profile;
  

  @Override
  protected void onCreate(Bundle state) {
    super.onCreate(state);
    setContentView(R.layout.activity_profile);
    
    vProfileName = (EditText) findViewById(R.id.v_profile_name);
    vEmail = (TextView) findViewById(R.id.v_email);
    vName = (TextView) findViewById(R.id.v_name);
    vSurname = (TextView) findViewById(R.id.v_surname);
    vBirthDate = (TextView) findViewById(R.id.v_birth_date);
    vGender = (Spinner) findViewById(R.id.v_gender);
    vHeight = (EditText) findViewById(R.id.v_height);
    vActivityLevel = (RadioGroup) findViewById(R.id.v_activity_level);
    vLifetimeAthlete = (CheckBox) findViewById(R.id.v_lifetime_athlete);
    
    vGender.setAdapter(new ArrayAdapter<Gender>(this, android.R.layout.simple_spinner_dropdown_item, Gender.values()));
    
    dateFormat = SimpleDateFormat.getDateInstance();
    
    dbProfile = Application.getInstance().getDatabase().getProfileTable();
    
    if(state == null) {
      long profileID = getIntent().getLongExtra(EXTRA_PROFILE_ID, -1L);
      if(profileID > 0L) {
        try {
          profile = dbProfile.getProfile(profileID);
        }
        catch (EntryNotFoundException e) {
          AndroidUtils.toast(this, R.string.profile_profile_not_found);
          Log.e(TAG, "Failed to load profile with ID: " + profileID, e);
          finish();
          return;
        }
        catch (DatabaseException e) {
          AndroidUtils.toast(this, "Failed to load profile from database.");
          Log.e(TAG, "Failed to load profile from DB: profileID=" + profileID);
          finish();
          return;
        }
        
        setValues(profile);
        ((Button) findViewById(R.id.btn_save_profile)).setText(R.string.profile_update_profile);
      }
    }
    else {
      if(state.containsKey(STATE_SELECTED_DATE)){
        selectedDate = Calendar.getInstance();
        selectedDate.setTimeInMillis(state.getLong(STATE_SELECTED_DATE));
        updateBirthDate();
      }
      profile = state.getParcelable(STATE_PROFILE);
    }
  }

  private void setValues(Profile profile) {
    vProfileName.setText(profile.getProfileName());
    vEmail.setText(profile.getEmail());
    vName.setText(profile.getName());
    vSurname.setText(profile.getSurname());
    selectedDate = profile.getBirthDate();
    updateBirthDate();
    vGender.setSelection(profile.getGender().getIndex());
    vHeight.setText(String.valueOf(profile.getHeight()));
    vActivityLevel.check(
        vActivityLevel.getChildAt(profile.getActivityLevel() -1).getId());
    vLifetimeAthlete.setChecked(profile.isLifetimeAthlete());
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if(selectedDate != null)
      outState.putLong(STATE_SELECTED_DATE, selectedDate.getTimeInMillis());
  }
  

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.profile, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Log.d(TAG, "Menu item selected: " + item);
    switch (item.getItemId()) {
      case R.id.action_connect_eegbase:
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_EMAIL, vEmail.getText().toString());
        startActivityForResult(intent, REQUEST_LOGIN);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case REQUEST_LOGIN:
        if(resultCode == RESULT_OK) {
          vEmail.setText(data.getStringExtra(LoginActivity.EXTRA_EMAIL));
          vName.setText(data.getStringExtra(LoginActivity.EXTRA_NAME));
          vSurname.setText(data.getStringExtra(LoginActivity.EXTRA_SURNAME));
        }
        break;
  
      default:
        super.onActivityResult(requestCode, resultCode, data);
        break;
    }
  }

  public void saveProfile(View view){
    String profileName = vProfileName.getText().toString();
    Gender gender = (Gender) vGender.getSelectedItem();
    String email = vEmail.getText().toString();
    String name = vName.getText().toString();
    String surname = vSurname.getText().toString();
    
    String heightString = vHeight.getText().toString();
    int height = heightString.isEmpty() ? -1 : Integer.parseInt(heightString);
    
    int activityLevel = Integer.parseInt(((RadioButton)findViewById(vActivityLevel.getCheckedRadioButtonId())).getText().toString());
    boolean lifetimeAthlete = vLifetimeAthlete.isChecked();
    
    if(profileName.length() == 0) {
      AndroidUtils.toast(this, R.string.profile_specify_profile_name);
      return;
    }
    if(selectedDate == null) {
      AndroidUtils.toast(this, R.string.profile_specify_birth_date);
      return;
    }
    if(height < 0) {
      AndroidUtils.toast(this, R.string.profile_specify_height);
      return;
    }
    
    if(profile == null)
      profile = new Profile();
    
    profile.setProfileName(profileName);
    profile.setEmail(email);
    profile.setName(name);
    profile.setSurname(surname);
    profile.setBirthDate(selectedDate);
    profile.setGender(gender);
    profile.setHeight(height);
    profile.setActivityLevel(activityLevel);
    profile.setLifetimeAthlete(lifetimeAthlete);
    
    try {
      if(profile.getId() > 0L)
        dbProfile.updateProfile(profile);
      else
        dbProfile.createProfile(profile);
    }
    catch(DuplicateEntryException e) {
      if(ProfileTable.COLUMN_PROFILE_NAME.equals(e.getColumnName())) {
        AndroidUtils.toast(this, R.string.profile_duplicit_profile_name);
        vProfileName.requestFocus();
      }
      else if(ProfileTable.COLUMN_EMAIL.equals(e.getColumnName())) {
        AndroidUtils.toast(this, R.string.profile_duplicit_email);
        vEmail.requestFocus();
      }
      Log.i(TAG, "Tried to save profile with duplicit value.", e);
      return;
    }
    catch(DatabaseException e) {
      AndroidUtils.toast(this, R.string.database_error);
      Log.e(TAG, "Failed to create new profile with name: " + profileName, e);
      return;
    }

    setResult(RESULT_OK);
    finish();
  }
  
  public void onSelectBirthDate(View view) {
    DialogFragment dialog = new DatePickerFragment();
    if(selectedDate != null) {
      Bundle args = new Bundle(1);
      args.putLong(DatePickerFragment.ARG_INITIAL_DATE, selectedDate.getTimeInMillis());
      dialog.setArguments(args);
    }
    dialog.show(getFragmentManager(), "datePicker");
  }

  @Override
  public void onDateSet(DatePicker view, int year, int month, int day) {
    selectedDate = DateUtils.getDate(year, month, day);
    updateBirthDate();
  }
  
  private void updateBirthDate() {
    vBirthDate.setText(dateFormat.format(selectedDate.getTime()));
  }
}
