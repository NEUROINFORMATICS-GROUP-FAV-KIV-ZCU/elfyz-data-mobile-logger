package cz.zcu.kiv.mobile.logger.eegbase.upload_helpers;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateManufacturerAndSerialTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtil;


public class HRManufacturerAndSerialDbUploadHelper extends ADbUploadHelper {
  private HeartRateManufacturerAndSerialTable db;

  protected int iTime;
  protected int iManufacturerID;
  protected int iSerialNr;
  protected int iUploaded;
  

  public HRManufacturerAndSerialDbUploadHelper() { }
  
  public HRManufacturerAndSerialDbUploadHelper(String parameterName, String valueString, long[] ids, boolean append) {
    super(parameterName, valueString, ids, append);
  }
  
  public HRManufacturerAndSerialDbUploadHelper(String parameterName, Double valueInteger, long[] ids, boolean append) {
    super(parameterName, valueInteger, ids, append);
  }
  
  public HRManufacturerAndSerialDbUploadHelper(Parcel source) {
    super(source);
  }
  

  @Override
  protected Cursor init() throws UploadHelperException {
    db = Application.getInstance().getDatabase().getHeartRateManufacturerAndSerialTable();
    Cursor c = null;
    try {
      c = db.getMeasurements(ids);

      iTime =           c.getColumnIndexOrThrow(HeartRateManufacturerAndSerialTable.COLUMN_TIME);
      iManufacturerID = c.getColumnIndexOrThrow(HeartRateManufacturerAndSerialTable.COLUMN_MANUFACTURER_ID);
      iSerialNr =       c.getColumnIndexOrThrow(HeartRateManufacturerAndSerialTable.COLUMN_SERIAL_NUMBER);
      iUploaded =       c.getColumnIndexOrThrow(HeartRateManufacturerAndSerialTable.COLUMN_UPLOADED);
      
      return c;
    }
    catch (DatabaseException e) {
      CloseUtil.close(c);
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
      value.put("manufID", c.getInt(iManufacturerID));
      value.put("serialNr", c.getInt(iSerialNr));
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

  

  public static final Parcelable.Creator<HRManufacturerAndSerialDbUploadHelper> CREATOR = new Creator<HRManufacturerAndSerialDbUploadHelper>() {
    @Override
    public HRManufacturerAndSerialDbUploadHelper[] newArray(int size) {
      return new HRManufacturerAndSerialDbUploadHelper[size];
    }
    @Override
    public HRManufacturerAndSerialDbUploadHelper createFromParcel(Parcel source) {
      return new HRManufacturerAndSerialDbUploadHelper(source);
    }
  };
}
