package cz.zcu.kiv.mobile.logger.eegbase.upload_helpers;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleProductInformationTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public class WSProductInformationDbUploadHelper extends ADbUploadHelper {
  private WeightScaleProductInformationTable db;

  protected int iTime;
  protected int iVersionMain;
  protected int iVersionSupp;
  protected int iSerialNr;
  protected int iUploaded;
  

  public WSProductInformationDbUploadHelper() { }
  
  public WSProductInformationDbUploadHelper(String parameterName, String valueString, long[] ids, boolean append) {
    super(parameterName, valueString, ids, append);
  }
  
  public WSProductInformationDbUploadHelper(String parameterName, Double valueInteger, long[] ids, boolean append) {
    super(parameterName, valueInteger, ids, append);
  }
  
  public WSProductInformationDbUploadHelper(Parcel source) {
    super(source);
  }
  

  @Override
  protected Cursor init() throws UploadHelperException {
    db = Application.getInstance().getDatabase().getWeightScaleProductInformationTable();
    Cursor c = null;
    try {
      c = db.getMeasurements(ids);

      iTime =        c.getColumnIndexOrThrow(WeightScaleProductInformationTable.COLUMN_TIME);
      iVersionMain = c.getColumnIndexOrThrow(WeightScaleProductInformationTable.COLUMN_MAIN_SW_REV);
      iVersionSupp = c.getColumnIndexOrThrow(WeightScaleProductInformationTable.COLUMN_SUPP_SW_REV);
      iSerialNr =    c.getColumnIndexOrThrow(WeightScaleProductInformationTable.COLUMN_SERIAL_NR);
      iUploaded =    c.getColumnIndexOrThrow(WeightScaleProductInformationTable.COLUMN_UPLOADED);
      
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
      value.put("versionMain", c.getInt(iVersionMain));
      value.put("versionSupp", c.getInt(iVersionSupp));
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


  
  public static final Parcelable.Creator<WSProductInformationDbUploadHelper> CREATOR = new Creator<WSProductInformationDbUploadHelper>() {
    @Override
    public WSProductInformationDbUploadHelper[] newArray(int size) {
      return new WSProductInformationDbUploadHelper[size];
    }
    @Override
    public WSProductInformationDbUploadHelper createFromParcel(Parcel source) {
      return new WSProductInformationDbUploadHelper(source);
    }
  };
}
