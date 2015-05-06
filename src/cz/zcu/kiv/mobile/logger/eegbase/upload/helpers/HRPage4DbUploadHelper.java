package cz.zcu.kiv.mobile.logger.eegbase.upload.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.HeartRatePage4Table;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public class HRPage4DbUploadHelper extends ADbUploadHelper {
  private HeartRatePage4Table db;

  protected int iTime;
  protected int iManufSpecByte;
  protected int iPrevBeat;
  protected int iUploaded;
  

  public HRPage4DbUploadHelper() { }
  
  public HRPage4DbUploadHelper(String parameterName, String valueString, long[] ids, boolean append) {
    super(parameterName, valueString, ids, append);
  }
  
  public HRPage4DbUploadHelper(String parameterName, Double valueInteger, long[] ids, boolean append) {
    super(parameterName, valueInteger, ids, append);
  }
  
  public HRPage4DbUploadHelper(Parcel source) {
    super(source);
  }
  

  @Override
  protected Cursor init() throws UploadHelperException {
    db = Application.getInstance().getDatabase().getHeartRatePage4Table();
    Cursor c = null;
    try {
      c = db.getMeasurements(ids);

      iTime =           c.getColumnIndexOrThrow(HeartRatePage4Table.COLUMN_TIME);
      iManufSpecByte =  c.getColumnIndexOrThrow(HeartRatePage4Table.COLUMN_MANUFACTURER_SPECIFIC);
      iPrevBeat =       c.getColumnIndexOrThrow(HeartRatePage4Table.COLUMN_PREVIOUS_HB_TIME);
      iUploaded =       c.getColumnIndexOrThrow(HeartRatePage4Table.COLUMN_UPLOADED);
      
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
      value.put("manufSpec", c.getInt(iManufSpecByte));
      value.put("prevBeat", c.getInt(iPrevBeat));
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

  

  public static final Parcelable.Creator<HRPage4DbUploadHelper> CREATOR = new Creator<HRPage4DbUploadHelper>() {
    @Override
    public HRPage4DbUploadHelper[] newArray(int size) {
      return new HRPage4DbUploadHelper[size];
    }
    @Override
    public HRPage4DbUploadHelper createFromParcel(Parcel source) {
      return new HRPage4DbUploadHelper(source);
    }
  };
}
