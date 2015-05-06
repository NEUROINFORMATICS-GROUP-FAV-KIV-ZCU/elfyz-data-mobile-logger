package cz.zcu.kiv.mobile.logger.eegbase;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.types.AsyncTaskResult;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.CommunicationException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.WrongCredentialsException;
import cz.zcu.kiv.mobile.logger.eegbase.types.login.UserInfo;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


/**
 * Activity which displays a login screen to the user, offering registration as well.
 */
public class LoginActivity extends Activity {
  /**
   * The default email to populate the email field with.
   */
  public static final String EXTRA_EMAIL = "extra_email";
  public static final String EXTRA_NAME= "extra_name";
  public static final String EXTRA_SURNAME = "extra_surname";
  public static final String EXTRA_EEGBASE_PASSWORD = "extra_eegbase_password";
  
  private static final String TAG = LoginActivity.class.getSimpleName();

  /**
   * Keep track of the login task to ensure we can cancel it if requested.
   */
  private UserLoginTask mAuthTask = null;

  // UI references.
  private EditText mEmailView;
  private EditText mPasswordView;
  private View mLoginFormView;
  private View mLoginStatusView;
  private TextView mLoginStatusMessageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);
    setupActionBar();

    // Set up the login form.
    mEmailView = (EditText) findViewById(R.id.email);
    mEmailView.setText(getIntent().getStringExtra(EXTRA_EMAIL));

    mPasswordView = (EditText) findViewById(R.id.password);
    mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
              attemptLogin(textView);
              return true;
            }
            return false;
          }
        });

    mLoginFormView = findViewById(R.id.login_form);
    mLoginStatusView = findViewById(R.id.login_status);
    mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
  }
  
  @Override
  protected void onDestroy() {
    if(mAuthTask != null) {
      mAuthTask.cancel(true);
    }
    super.onDestroy();
  }

  /**
   * Set up the {@link android.app.ActionBar}, if the API is available.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private void setupActionBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      // Show the Up button in the action bar.
      getActionBar().setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.login, menu);
    return true;
  }

  /**
   * Attempts to sign in or register the account specified by the login form. If
   * there are form errors (invalid email, missing fields, etc.), the errors are
   * presented and no actual login attempt is made.
   */
  public void attemptLogin(View view) {
    if (mAuthTask != null) {
      return;
    }

    // Reset errors.
    mEmailView.setError(null);
    mPasswordView.setError(null);

    // Store values at the time of the login attempt.
    String email = mEmailView.getText().toString();
    String password = mPasswordView.getText().toString();

    boolean cancel = false;
    View focusView = null;

    // Check for a valid password.
    if (TextUtils.isEmpty(password)) {
      mPasswordView.setError(getString(R.string.error_field_required));
      focusView = mPasswordView;
      cancel = true;
    }
    else if (password.length() < 4) {
      mPasswordView.setError(getString(R.string.error_invalid_password));
      focusView = mPasswordView;
      cancel = true;
    }

    // Check for a valid email address.
    if (TextUtils.isEmpty(email)) {
      mEmailView.setError(getString(R.string.error_field_required));
      focusView = mEmailView;
      cancel = true;
    }
    else if (!email.contains("@")) {
      mEmailView.setError(getString(R.string.error_invalid_email));
      focusView = mEmailView;
      cancel = true;
    }

    if (cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    }
    else {
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
      showProgress(true);
      mAuthTask = new UserLoginTask(email, password);
      mAuthTask.execute();
    }
  }

  /**
   * Shows the progress UI and hides the login form.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private void showProgress(final boolean show) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(
          android.R.integer.config_shortAnimTime);

      mLoginStatusView.setVisibility(View.VISIBLE);
      mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
          .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
              mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
          });

      mLoginFormView.setVisibility(View.VISIBLE);
      mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
          .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
              mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
          });
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
  }

  /**
   * Represents an asynchronous login/registration task used to authenticate the user.
   */
  public class UserLoginTask extends AsyncTask<Void, Void, AsyncTaskResult<UserInfo>> {
    private final String email;
    private final String password;
    
    public UserLoginTask(String email, String password) {
      this.email = email;
      this.password = password;
    }
    
    @Override
    protected AsyncTaskResult<UserInfo> doInBackground(Void... params) {
      AsyncTaskResult<UserInfo> result = new AsyncTaskResult<UserInfo>();
      try {
        UserInfo info = EegbaseRest.login(email, password);
        result.setResult(info);
      }
      catch (WrongCredentialsException e) {
        result.setError(e);
      }
      catch (CommunicationException e) {
        result.setError(e);
      }
      return result;
    }

    @Override
    protected void onPostExecute(final AsyncTaskResult<UserInfo> result) {
      mAuthTask = null;
      showProgress(false);

      if (result.getError() != null) {
        Exception error = result.getError();
        if(error instanceof WrongCredentialsException) {
          Log.i(TAG, "Wrong credentials.", error);
          AndroidUtils.toast(LoginActivity.this, R.string.fail_login_credentials);
          mPasswordView.setError(getString(R.string.error_incorrect_password));
          mPasswordView.requestFocus();
        }
        else {
          Log.e(TAG, "Failed to communicate.", error);
          AndroidUtils.toast(LoginActivity.this, R.string.fail_communication);
        }
      }
      else {
        UserInfo info = result.getResult();
        
        Intent data = new Intent();
        data.putExtra(EXTRA_EMAIL, email);
        data.putExtra(EXTRA_NAME, info.getName());
        data.putExtra(EXTRA_SURNAME, info.getSurname());
        data.putExtra(EXTRA_EEGBASE_PASSWORD, password);
        setResult(RESULT_OK, data);
        finish();
      }
    }

    @Override
    protected void onCancelled() {
      mAuthTask = null;
      showProgress(false);
    }
  }
}
