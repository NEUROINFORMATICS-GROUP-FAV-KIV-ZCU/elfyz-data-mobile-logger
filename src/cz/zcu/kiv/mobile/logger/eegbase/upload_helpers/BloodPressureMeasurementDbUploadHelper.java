package cz.zcu.kiv.mobile.logger.eegbase.upload_helpers;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.BloodPressureMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public class BloodPressureMeasurementDbUploadHelper extends ADbUploadHelper {
  private BloodPressureMeasurementTable db;

  private int iTime;
  private int iSystolic;
  private int iDiastolic;
  private int iMeanPressure;
  private int iHeartRate;
  

  public BloodPressureMeasurementDbUploadHelper() { }
  
  public BloodPressureMeasurementDbUploadHelper(String parameterName, String valueString, long[] ids, boolean append) {
    super(parameterName, valueString, ids, append);
  }
  
  public BloodPressureMeasurementDbUploadHelper(String parameterName, Double valueInteger, long[] ids, boolean append) {
    super(parameterName, valueInteger, ids, append);
  }
  
  public BloodPressureMeasurementDbUploadHelper(Parcel source) {
    super(source);
  }
  

  @Override
  protected Cursor init() throws UploadHelperException {
    db = Application.getInstance().getDatabase().getBloodPressureMeasurementTable();
    Cursor c = null;
    try {
      c = db.getMeasurements(ids);
      
      iTime =         c.getColumnIndexOrThrow(BloodPressureMeasurementTable.COLUMN_TIME);
      iSystolic =     c.getColumnIndexOrThrow(BloodPressureMeasurementTable.COLUMN_SYSTOLIC);
      iDiastolic =    c.getColumnIndexOrThrow(BloodPressureMeasurementTable.COLUMN_DIASTOLIC);
      iMeanPressure = c.getColumnIndexOrThrow(BloodPressureMeasurementTable.COLUMN_MEAN_PRESSURE);
      iHeartRate =    c.getColumnIndexOrThrow(BloodPressureMeasurementTable.COLUMN_HEART_RATE);
      
      return c;
    }
    catch (DatabaseException e) {
      CloseUtils.close(c);
      throw new UploadHelperException("Failed to load data from database.", e);
    }
  }

  @Override
  protected String getID(Cursor data) {
    return data.getString(iTime);
  }
  
  @Override
  protected String toJSON(Cursor c) throws JSONException {
    JSONObject value = new JSONObject();
      value.put("time", c.getLong(iTime));
      value.put("sys", c.getInt(iSystolic));
      value.put("dia", c.getInt(iDiastolic));
      value.put("mean", c.getInt(iMeanPressure));
      value.put("heartRate", c.getInt(iHeartRate));
    return value.toString();
  }

  @Override
  public void markUploaded() throws UploadHelperException {
    try {
      db.setUploaded(ids);
    }
    catch (DatabaseException e) {
      throw new UploadHelperException("Failed to mark records as uploaded.", e);
    }
  }


  
  public static final Parcelable.Creator<BloodPressureMeasurementDbUploadHelper> CREATOR = new Creator<BloodPressureMeasurementDbUploadHelper>() {
    @Override
    public BloodPressureMeasurementDbUploadHelper[] newArray(int size) {
      return new BloodPressureMeasurementDbUploadHelper[size];
    }
    @Override
    public BloodPressureMeasurementDbUploadHelper createFromParcel(Parcel source) {
      return new BloodPressureMeasurementDbUploadHelper(source);
    }
  };
}
