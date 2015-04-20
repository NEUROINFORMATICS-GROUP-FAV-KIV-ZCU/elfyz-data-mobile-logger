package cz.zcu.kiv.mobile.logger.eegbase.upload_helpers;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateCalculatedRrIntervalTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public class HRCalculatedRrIntervalDbUploadHelper extends ADbUploadHelper {
  private HeartRateCalculatedRrIntervalTable db;

  protected int iTime;
  protected int iRrFlag;
  protected int iRrInterval;
  protected int iUploaded;
  

  public HRCalculatedRrIntervalDbUploadHelper() { }
  
  public HRCalculatedRrIntervalDbUploadHelper(String parameterName, String valueString, long[] ids, boolean append) {
    super(parameterName, valueString, ids, append);
  }
  
  public HRCalculatedRrIntervalDbUploadHelper(String parameterName, Double valueInteger, long[] ids, boolean append) {
    super(parameterName, valueInteger, ids, append);
  }
  
  public HRCalculatedRrIntervalDbUploadHelper(Parcel source) {
    super(source);
  }
  

  @Override
  protected Cursor init() throws UploadHelperException {
    db = Application.getInstance().getDatabase().getHeartRateCalculatedRrIntervalTable();
    Cursor c = null;
    try {
      c = db.getMeasurements(ids);

      iTime =       c.getColumnIndexOrThrow(HeartRateCalculatedRrIntervalTable.COLUMN_TIME);
      iRrFlag =     c.getColumnIndexOrThrow(HeartRateCalculatedRrIntervalTable.COLUMN_RR_FLAG);
      iRrInterval = c.getColumnIndexOrThrow(HeartRateCalculatedRrIntervalTable.COLUMN_CALC_RR_INTERVAL);
      iUploaded =   c.getColumnIndexOrThrow(HeartRateCalculatedRrIntervalTable.COLUMN_UPLOADED);
      
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
      value.put("rrFlag", HeartRateCalculatedRrIntervalTable.mapRrFlag(c.getInt(iRrFlag)).toString());
      value.put("rrInterval", HeartRateCalculatedRrIntervalTable.mapRrFlag(c.getInt(iRrInterval)).toString());
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

  

  public static final Parcelable.Creator<HRCalculatedRrIntervalDbUploadHelper> CREATOR = new Creator<HRCalculatedRrIntervalDbUploadHelper>() {
    @Override
    public HRCalculatedRrIntervalDbUploadHelper[] newArray(int size) {
      return new HRCalculatedRrIntervalDbUploadHelper[size];
    }
    @Override
    public HRCalculatedRrIntervalDbUploadHelper createFromParcel(Parcel source) {
      return new HRCalculatedRrIntervalDbUploadHelper(source);
    }
  };
}
