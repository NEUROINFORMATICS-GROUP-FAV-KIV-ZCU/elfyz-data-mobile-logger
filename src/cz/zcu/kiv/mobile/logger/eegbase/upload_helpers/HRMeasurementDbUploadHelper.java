package cz.zcu.kiv.mobile.logger.eegbase.upload_helpers;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public class HRMeasurementDbUploadHelper extends ADbUploadHelper {
  private HeartRateMeasurementTable db;

  protected int iTime;
  protected int iDataState;
  protected int iHeartRate;
  protected int iBeatCount;
  protected int iBeatTime;
  protected int iUploaded;
  

  public HRMeasurementDbUploadHelper() { }
  
  public HRMeasurementDbUploadHelper(String parameterName, String valueString, long[] ids, boolean append) {
    super(parameterName, valueString, ids, append);
  }
  
  public HRMeasurementDbUploadHelper(String parameterName, Double valueInteger, long[] ids, boolean append) {
    super(parameterName, valueInteger, ids, append);
  }
  
  public HRMeasurementDbUploadHelper(Parcel source) {
    super(source);
  }
  

  @Override
  protected Cursor init() throws UploadHelperException {
    db = Application.getInstance().getDatabase().getHeartRateMeasurementTable();
    Cursor c = null;
    try {
      c = db.getMeasurements(ids);

      iTime =      c.getColumnIndexOrThrow(HeartRateMeasurementTable.COLUMN_TIME);
      iDataState = c.getColumnIndexOrThrow(HeartRateMeasurementTable.COLUMN_DATA_STATE);
      iHeartRate = c.getColumnIndexOrThrow(HeartRateMeasurementTable.COLUMN_HEART_RATE);
      iBeatCount = c.getColumnIndexOrThrow(HeartRateMeasurementTable.COLUMN_BEAT_COUNT);
      iBeatTime =  c.getColumnIndexOrThrow(HeartRateMeasurementTable.COLUMN_BEAT_TIME);
      iUploaded =  c.getColumnIndexOrThrow(HeartRateMeasurementTable.COLUMN_UPLOADED);
      
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
      value.put("dataState", HeartRateMeasurementTable.mapDataState(c.getInt(iDataState)).toString());
      value.put("heartRate", c.getInt(iHeartRate));
      value.put("beatCount", c.getInt(iBeatCount));
      value.put("beatTime", c.getInt(iBeatTime));
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

  

  public static final Parcelable.Creator<HRMeasurementDbUploadHelper> CREATOR = new Creator<HRMeasurementDbUploadHelper>() {
    @Override
    public HRMeasurementDbUploadHelper[] newArray(int size) {
      return new HRMeasurementDbUploadHelper[size];
    }
    @Override
    public HRMeasurementDbUploadHelper createFromParcel(Parcel source) {
      return new HRMeasurementDbUploadHelper(source);
    }
  };
}
