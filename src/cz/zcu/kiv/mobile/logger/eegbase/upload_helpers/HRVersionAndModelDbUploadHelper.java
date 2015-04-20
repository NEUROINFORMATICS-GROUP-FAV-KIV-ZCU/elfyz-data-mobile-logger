package cz.zcu.kiv.mobile.logger.eegbase.upload_helpers;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateVersionAndModelTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public class HRVersionAndModelDbUploadHelper extends ADbUploadHelper {
  private HeartRateVersionAndModelTable db;

  protected int iTime;
  protected int iHW;
  protected int iSW;
  protected int iModelNr;
  protected int iUploaded;
  

  public HRVersionAndModelDbUploadHelper() { }
  
  public HRVersionAndModelDbUploadHelper(String parameterName, String valueString, long[] ids, boolean append) {
    super(parameterName, valueString, ids, append);
  }
  
  public HRVersionAndModelDbUploadHelper(String parameterName, Double valueInteger, long[] ids, boolean append) {
    super(parameterName, valueInteger, ids, append);
  }
  
  public HRVersionAndModelDbUploadHelper(Parcel source) {
    super(source);
  }
  

  @Override
  protected Cursor init() throws UploadHelperException {
    db = Application.getInstance().getDatabase().getHeartRateVersionAndModelTable();
    Cursor c = null;
    try {
      c = db.getMeasurements(ids);

      iTime =     c.getColumnIndexOrThrow(HeartRateVersionAndModelTable.COLUMN_TIME);
      iHW =       c.getColumnIndexOrThrow(HeartRateVersionAndModelTable.COLUMN_HW_VERSION);
      iSW =       c.getColumnIndexOrThrow(HeartRateVersionAndModelTable.COLUMN_SW_VERSION);
      iModelNr =  c.getColumnIndexOrThrow(HeartRateVersionAndModelTable.COLUMN_MODEL_NUMBER);
      iUploaded = c.getColumnIndexOrThrow(HeartRateVersionAndModelTable.COLUMN_UPLOADED);
      
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
      value.put("hwVersion", c.getInt(iHW));
      value.put("swVersion", c.getInt(iSW));
      value.put("modelNr", c.getInt(iModelNr));
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

  

  public static final Parcelable.Creator<HRVersionAndModelDbUploadHelper> CREATOR = new Creator<HRVersionAndModelDbUploadHelper>() {
    @Override
    public HRVersionAndModelDbUploadHelper[] newArray(int size) {
      return new HRVersionAndModelDbUploadHelper[size];
    }
    @Override
    public HRVersionAndModelDbUploadHelper createFromParcel(Parcel source) {
      return new HRVersionAndModelDbUploadHelper(source);
    }
  };
}
