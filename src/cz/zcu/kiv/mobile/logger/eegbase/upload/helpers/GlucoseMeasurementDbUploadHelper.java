package cz.zcu.kiv.mobile.logger.eegbase.upload.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.GlucoseMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public class GlucoseMeasurementDbUploadHelper extends ADbUploadHelper {
  private GlucoseMeasurementTable db;

  protected int iTime;
  protected int iGlucose;
  protected int iTemperature;
  protected int iCode;
  protected int iType;
  

  public GlucoseMeasurementDbUploadHelper() { }
  
  public GlucoseMeasurementDbUploadHelper(String parameterName, String valueString, long[] ids, boolean append) {
    super(parameterName, valueString, ids, append);
  }
  
  public GlucoseMeasurementDbUploadHelper(String parameterName, Double valueInteger, long[] ids, boolean append) {
    super(parameterName, valueInteger, ids, append);
  }
  
  public GlucoseMeasurementDbUploadHelper(Parcel source) {
    super(source);
  }
  

  @Override
  public Cursor init() throws UploadHelperException {
    db = Application.getInstance().getDatabase().getGlucoseMeasurementTable();
    Cursor c = null;
    try {
      c = db.getMeasurements(ids);

      iTime =        c.getColumnIndexOrThrow(GlucoseMeasurementTable.COLUMN_TIME);
      iGlucose =     c.getColumnIndexOrThrow(GlucoseMeasurementTable.COLUMN_GLUCOSE);
      iTemperature = c.getColumnIndexOrThrow(GlucoseMeasurementTable.COLUMN_TEMPERATURE);
      iCode =        c.getColumnIndexOrThrow(GlucoseMeasurementTable.COLUMN_CODE);
      iType =        c.getColumnIndexOrThrow(GlucoseMeasurementTable.COLUMN_TYPE);
      
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
      value.put("glucose", c.getInt(iGlucose));
      value.put("temp", c.getInt(iTemperature));
      value.put("code", c.getInt(iCode));
      value.put("type", c.getInt(iType));
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

  

  public static final Parcelable.Creator<GlucoseMeasurementDbUploadHelper> CREATOR = new Creator<GlucoseMeasurementDbUploadHelper>() {
    @Override
    public GlucoseMeasurementDbUploadHelper[] newArray(int size) {
      return new GlucoseMeasurementDbUploadHelper[size];
    }
    @Override
    public GlucoseMeasurementDbUploadHelper createFromParcel(Parcel source) {
      return new GlucoseMeasurementDbUploadHelper(source);
    }
  };
}
