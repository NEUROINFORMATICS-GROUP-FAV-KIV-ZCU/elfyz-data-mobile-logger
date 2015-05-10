package cz.zcu.kiv.mobile.logger.utils;

import java.util.Calendar;
import java.util.Date;


public class DateUtils {

  private DateUtils() {}
  
  
  public static Calendar getDate(int year, int month, int day) {
    Calendar date = Calendar.getInstance();
      date.set(Calendar.YEAR, year);
      date.set(Calendar.MONTH, month);
      date.set(Calendar.DAY_OF_MONTH, day);
      date.set(Calendar.HOUR_OF_DAY, 0);
      date.set(Calendar.MINUTE, 0);
      date.set(Calendar.SECOND, 0);
      date.set(Calendar.MILLISECOND, 0);
      
    return date;
  }
  
  public static Calendar getDate(Long milis){
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(milis);
    return cal;
  }

  public static Date addDays(Date day, int addDays) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(day.getTime());
    cal.add(Calendar.DAY_OF_MONTH, addDays);
    return cal.getTime();
  }
}
