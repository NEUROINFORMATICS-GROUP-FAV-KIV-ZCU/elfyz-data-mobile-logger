package cz.zcu.kiv.mobile.logger.eegbase.upload_helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleManufacturerSpecificDataTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public class WSManufacturerSpecificDataDbUploadHelper extends ADbUploadHelper {
  private WeightScaleManufacturerSpecificDataTable db;

  protected int iTime;
  protected int iManufSpecific;
  protected int iUploaded;
  

  public WSManufacturerSpecificDataDbUploadHelper() { }
  
  public WSManufacturerSpecificDataDbUploadHelper(String parameterName, String valueString, long[] ids, boolean append) {
    super(parameterName, valueString, ids, append);
  }
  
  public WSManufacturerSpecificDataDbUploadHelper(String parameterName, Double valueInteger, long[] ids, boolean append) {
    super(parameterName, valueInteger, ids, append);
  }
  
  public WSManufacturerSpecificDataDbUploadHelper(Parcel source) {
    super(source);
  }
  

  @Override
  protected Cursor init() throws UploadHelperException {
    db = Application.getInstance().getDatabase().getWeightScaleManufacturerSpecificDataTable();
    Cursor c = null;
    try {
      c = db.getMeasurements(ids);

      iTime =          c.getColumnIndexOrThrow(WeightScaleManufacturerSpecificDataTable.COLUMN_TIME);
      iManufSpecific = c.getColumnIndexOrThrow(WeightScaleManufacturerSpecificDataTable.COLUMN_DATA);
      iUploaded =      c.getColumnIndexOrThrow(WeightScaleManufacturerSpecificDataTable.COLUMN_UPLOADED);
      
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
    byte[] data = c.getBlob(iManufSpecific);

    JSONArray array = new JSONArray();
    for (int i = 0; i < data.length; i++) {
      array.put(data[i]);
    }
      
    JSONObject value = new JSONObject();
      value.put("time", c.getLong(iTime));
      value.put("length", data.length);
      value.put("data", array);
      
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


  
  public static final Parcelable.Creator<WSManufacturerSpecificDataDbUploadHelper> CREATOR = new Creator<WSManufacturerSpecificDataDbUploadHelper>() {
    @Override
    public WSManufacturerSpecificDataDbUploadHelper[] newArray(int size) {
      return new WSManufacturerSpecificDataDbUploadHelper[size];
    }
    @Override
    public WSManufacturerSpecificDataDbUploadHelper createFromParcel(Parcel source) {
      return new WSManufacturerSpecificDataDbUploadHelper(source);
    }
  };
}
