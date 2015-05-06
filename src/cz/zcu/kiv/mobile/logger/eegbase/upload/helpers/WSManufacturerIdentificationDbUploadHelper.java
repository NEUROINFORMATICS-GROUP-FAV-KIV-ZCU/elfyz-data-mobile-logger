package cz.zcu.kiv.mobile.logger.eegbase.upload.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleManufacturerIdentificationTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public class WSManufacturerIdentificationDbUploadHelper extends ADbUploadHelper {
  private WeightScaleManufacturerIdentificationTable db;

  protected int iTime;
  protected int iHwRev;
  protected int iManufacturerId;
  protected int iModelNumber;
  protected int iUploaded;
  

  public WSManufacturerIdentificationDbUploadHelper() { }
  
  public WSManufacturerIdentificationDbUploadHelper(String parameterName, String valueString, long[] ids, boolean append) {
    super(parameterName, valueString, ids, append);
  }
  
  public WSManufacturerIdentificationDbUploadHelper(String parameterName, Double valueInteger, long[] ids, boolean append) {
    super(parameterName, valueInteger, ids, append);
  }
  
  public WSManufacturerIdentificationDbUploadHelper(Parcel source) {
    super(source);
  }
  

  @Override
  protected Cursor init() throws UploadHelperException {
    db = Application.getInstance().getDatabase().getWeightScaleManufacturerIdentificationTable();
    Cursor c = null;
    try {
      c = db.getMeasurements(ids);

      iTime =           c.getColumnIndexOrThrow(WeightScaleManufacturerIdentificationTable.COLUMN_TIME);
      iHwRev =          c.getColumnIndexOrThrow(WeightScaleManufacturerIdentificationTable.COLUMN_HW_REV);
      iManufacturerId = c.getColumnIndexOrThrow(WeightScaleManufacturerIdentificationTable.COLUMN_MANUF_ID);
      iModelNumber =    c.getColumnIndexOrThrow(WeightScaleManufacturerIdentificationTable.COLUMN_MODEL_NR);
      iUploaded =       c.getColumnIndexOrThrow(WeightScaleManufacturerIdentificationTable.COLUMN_UPLOADED);
      
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
      value.put("hwRev", c.getInt(iHwRev));
      value.put("manufId", c.getInt(iManufacturerId));
      value.put("modelNr", c.getInt(iModelNumber));
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


  
  public static final Parcelable.Creator<WSManufacturerIdentificationDbUploadHelper> CREATOR = new Creator<WSManufacturerIdentificationDbUploadHelper>() {
    @Override
    public WSManufacturerIdentificationDbUploadHelper[] newArray(int size) {
      return new WSManufacturerIdentificationDbUploadHelper[size];
    }
    @Override
    public WSManufacturerIdentificationDbUploadHelper createFromParcel(Parcel source) {
      return new WSManufacturerIdentificationDbUploadHelper(source);
    }
  };
}
