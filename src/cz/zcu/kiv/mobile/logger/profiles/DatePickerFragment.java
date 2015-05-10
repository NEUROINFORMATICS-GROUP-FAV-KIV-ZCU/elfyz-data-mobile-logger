package cz.zcu.kiv.mobile.logger.profiles;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.utils.DateUtils;


public class DatePickerFragment extends DialogFragment {
  private static final String TAG = DatePickerFragment.class.getSimpleName();
  
  public static final String ARG_INITIAL_DATE = "arg.initial.date";

  private OnDateSetListener listener;
  
  
  public void setListener(OnDateSetListener listener) {
    this.listener = listener;
  }
  
  
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    if(listener == null) {
      Log.e(TAG, "Listener should be set.");
      dismiss();
    }
    
    Bundle args = getArguments();
    
    Calendar c = (args != null && args.containsKey(ARG_INITIAL_DATE))
                  ? DateUtils.getDate(args.getLong(ARG_INITIAL_DATE))
                  : Calendar.getInstance();
    
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
    
    return new DatePickerDialog(getActivity(), listener, year, month, day);
  }
}
