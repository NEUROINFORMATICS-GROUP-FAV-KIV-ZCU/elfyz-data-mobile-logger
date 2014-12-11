package cz.zcu.kiv.mobile.logger.profiles;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.database.Database;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DuplicateEntryException;
import cz.zcu.kiv.mobile.logger.data.types.Gender;
import cz.zcu.kiv.mobile.logger.utils.DateUtils;


public class ProfileActivity extends FragmentActivity implements OnDateSetListener {
  private static final String TAG = ProfileActivity.class.getSimpleName();
  
  private static final String SAVED_SELECTED_DATE = "save.selected.date";
  
  private EditText vProfileName;
  private TextView vBirthDate;
  private Spinner vGender;
  private EditText vHeight;
  private RadioGroup vActivityLevel;
  private CheckBox vLifetimeAthlete;

  private SimpleDateFormat dateFormat;
  
  private Database db;
  private Calendar selectedDate;
  

  @Override
  protected void onCreate(Bundle state) {
    super.onCreate(state);
    setContentView(R.layout.activity_profile);
    
    vProfileName = (EditText) findViewById(R.id.v_profile_name);
    vBirthDate = (TextView) findViewById(R.id.v_birth_date);
    vGender = (Spinner) findViewById(R.id.v_gender);
    vHeight = (EditText) findViewById(R.id.v_height);
    vActivityLevel = (RadioGroup) findViewById(R.id.v_activity_level);
    vLifetimeAthlete = (CheckBox) findViewById(R.id.v_lifetime_athlete);
    
    vGender.setAdapter(new ArrayAdapter<Gender>(this, android.R.layout.simple_spinner_dropdown_item, Gender.values()));
    
    dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    
    db = ((Application) getApplication()).getDatabase();
    
    if(state != null && state.containsKey(SAVED_SELECTED_DATE)){
      selectedDate = Calendar.getInstance();
      selectedDate.setTimeInMillis(state.getLong(SAVED_SELECTED_DATE));
      updateBirthDate();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if(selectedDate != null)
      outState.putLong(SAVED_SELECTED_DATE, selectedDate.getTimeInMillis());
  }
  

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.profile, menu);
    return true;
  }

  public void createProfile(View view){
    String profileName = vProfileName.getText().toString();
    Gender gender = (Gender) vGender.getSelectedItem();
    
    String heightString = vHeight.getText().toString();
    int height = heightString.isEmpty() ? -1 : Integer.parseInt(heightString);
    
    int activityLevel = Integer.parseInt(((RadioButton)findViewById(vActivityLevel.getCheckedRadioButtonId())).getText().toString());
    boolean lifetimeAthlete = vLifetimeAthlete.isChecked();
    
    if(profileName.length() == 0){
      Toast.makeText(this, R.string.profile_specify_profile_name, Toast.LENGTH_LONG).show();
      return;
    }
    if(selectedDate == null){
      Toast.makeText(this, R.string.profile_specify_birth_date, Toast.LENGTH_LONG).show();
      return;
    }
    if(height < 0){
      Toast.makeText(this, R.string.profile_specify_height, Toast.LENGTH_LONG).show();
      return;
    }
    
    try{
      db.createProfile(profileName, selectedDate, gender, height, activityLevel, lifetimeAthlete);
    }
    catch(DuplicateEntryException e){
      Toast.makeText(this, R.string.profile_duplicit_profile_name, Toast.LENGTH_LONG).show();
      Log.i(TAG, "Tried to create profile with duplicate name: " + profileName, e);
      return;
    }
    catch(DatabaseException e){
      Toast.makeText(this, R.string.database_error, Toast.LENGTH_LONG).show();
      Log.e(TAG, "Failed to create new profile with name: " + profileName, e);
      return;
    }

    setResult(RESULT_OK);
    finish();
  }
  
  public void onSelectBirthDate(View view){
    DialogFragment dialog = new DatePickerFragment();
    if(selectedDate != null) {
      Bundle args = new Bundle(1);
      args.putLong(DatePickerFragment.ARG_INITIAL_DATE, selectedDate.getTimeInMillis());
      dialog.setArguments(args);
    }
    dialog.show(getSupportFragmentManager(), "datePicker");
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
