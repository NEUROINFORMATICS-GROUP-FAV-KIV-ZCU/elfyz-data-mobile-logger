package cz.zcu.kiv.mobile.logger.eegbase.upload_helpers;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleBatteryStatusTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtil;


public class WSBatteryStatusDbUploadHelper extends ADbUploadHelper {
  private WeightScaleBatteryStatusTable db;

  protected int iTime;
  protected int iCumulOpTime;
  protected int iCumulOpTimeRes;
  protected int iBatteryVoltage;
  protected int iBatteryStatus;
  protected int iBatteryCount;
  protected int iBatteryId;
  protected int iUploaded;
  

  public WSBatteryStatusDbUploadHelper() { }
  
  public WSBatteryStatusDbUploadHelper(String parameterName, String valueString, long[] ids, boolean append) {
    super(parameterName, valueString, ids, append);
  }
  
  public WSBatteryStatusDbUploadHelper(String parameterName, Double valueInteger, long[] ids, boolean append) {
    super(parameterName, valueInteger, ids, append);
  }
  
  public WSBatteryStatusDbUploadHelper(Parcel source) {
    super(source);
  }
  

  @Override
  protected Cursor init() throws UploadHelperException {
    db = Application.getInstance().getDatabase().getWeightScaleBatteryStatusTable();
    Cursor c = null;
    try {
      c = db.getMeasurements(ids);

      iTime =           c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_TIME);
      iCumulOpTime =    c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_CUMUL_OP_TIME);
      iCumulOpTimeRes = c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_CUMUL_OP_TIME_RES);
      iBatteryVoltage = c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_BAT_VOLTAGE);
      iBatteryStatus =  c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_BAT_STATUS);
      iBatteryCount =   c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_BAT_COUNT);
      iBatteryId =      c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_BAT_ID);
      iUploaded =       c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_UPLOADED);
      
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
      value.put("cumulOpTime", c.getLong(iCumulOpTime));
      value.put("cumulOpTimeRes", c.getInt(iCumulOpTimeRes));
      value.put("batVoltage", c.getDouble(iBatteryVoltage));
      value.put("batStatus", WeightScaleBatteryStatusTable.mapBatteryStatus(c.getInt(iBatteryStatus)));
      value.put("batCount", c.getInt(iBatteryCount));
      value.put("batId", c.getInt(iBatteryId));
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


  
  public static final Parcelable.Creator<WSBatteryStatusDbUploadHelper> CREATOR = new Creator<WSBatteryStatusDbUploadHelper>() {
    @Override
    public WSBatteryStatusDbUploadHelper[] newArray(int size) {
      return new WSBatteryStatusDbUploadHelper[size];
    }
    @Override
    public WSBatteryStatusDbUploadHelper createFromParcel(Parcel source) {
      return new WSBatteryStatusDbUploadHelper(source);
    }
  };
}
