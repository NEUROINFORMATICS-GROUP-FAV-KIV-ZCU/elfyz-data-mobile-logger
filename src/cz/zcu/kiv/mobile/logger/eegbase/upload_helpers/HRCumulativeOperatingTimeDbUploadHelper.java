package cz.zcu.kiv.mobile.logger.eegbase.upload_helpers;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateCumulativeOperatingTimeTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public class HRCumulativeOperatingTimeDbUploadHelper extends ADbUploadHelper {
  private HeartRateCumulativeOperatingTimeTable db;

  protected int iTime;
  protected int iCumulOpTime;
  protected int iUploaded;
  

  public HRCumulativeOperatingTimeDbUploadHelper() { }
  
  public HRCumulativeOperatingTimeDbUploadHelper(String parameterName, String valueString, long[] ids, boolean append) {
    super(parameterName, valueString, ids, append);
  }
  
  public HRCumulativeOperatingTimeDbUploadHelper(String parameterName, Double valueInteger, long[] ids, boolean append) {
    super(parameterName, valueInteger, ids, append);
  }
  
  public HRCumulativeOperatingTimeDbUploadHelper(Parcel source) {
    super(source);
  }
  

  @Override
  protected Cursor init() throws UploadHelperException {
    db = Application.getInstance().getDatabase().getHeartRateCumulativeOperatingTimeTable();
    Cursor c = null;
    try {
      c = db.getMeasurements(ids);

      iTime =        c.getColumnIndexOrThrow(HeartRateCumulativeOperatingTimeTable.COLUMN_TIME);
      iCumulOpTime = c.getColumnIndexOrThrow(HeartRateCumulativeOperatingTimeTable.COLUMN_CUMUL_OP_TIME);
      iUploaded =    c.getColumnIndexOrThrow(HeartRateCumulativeOperatingTimeTable.COLUMN_UPLOADED);
      
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
      value.put("cumulOpTime", c.getInt(iCumulOpTime));
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

  

  public static final Parcelable.Creator<HRCumulativeOperatingTimeDbUploadHelper> CREATOR = new Creator<HRCumulativeOperatingTimeDbUploadHelper>() {
    @Override
    public HRCumulativeOperatingTimeDbUploadHelper[] newArray(int size) {
      return new HRCumulativeOperatingTimeDbUploadHelper[size];
    }
    @Override
    public HRCumulativeOperatingTimeDbUploadHelper createFromParcel(Parcel source) {
      return new HRCumulativeOperatingTimeDbUploadHelper(source);
    }
  };
}
