package cz.zcu.kiv.mobile.logger.eegbase.upload_helpers;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;


public class WSMeasurementDbUploadHelper extends ADbUploadHelper {
  private HeartRateMeasurementTable db;

  protected int iTime;
  protected int iBodyWeight;
  protected int iMuscleMass;
  protected int iBoneMass;
  protected int iFatPercentage;
  protected int iHydrationPercentage;
  protected int iActiveMetRate;
  protected int iBasalMetRate;
  protected int iUploaded;
  

  public WSMeasurementDbUploadHelper() { }
  
  public WSMeasurementDbUploadHelper(String parameterName, String valueString, long[] ids, boolean append) {
    super(parameterName, valueString, ids, append);
  }
  
  public WSMeasurementDbUploadHelper(String parameterName, Double valueInteger, long[] ids, boolean append) {
    super(parameterName, valueInteger, ids, append);
  }
  
  public WSMeasurementDbUploadHelper(Parcel source) {
    super(source);
  }
  

  @Override
  protected Cursor init() throws UploadHelperException {
    db = Application.getInstance().getDatabase().getHeartRateMeasurementTable();
    Cursor c = null;
    try {
      c = db.getMeasurements(ids);

      iTime =                c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_TIME);
      iBodyWeight =          c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_WEIGHT);
      iMuscleMass =          c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_MUSCLE_MASS);
      iBoneMass =            c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_BONE_MASS);
      iFatPercentage =       c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_FAT_PERCENTAGE);
      iHydrationPercentage = c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_HYDRATION_PERCENTAGE);
      iActiveMetRate =       c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_ACTIVE_METABOLIC_RATE);
      iBasalMetRate =        c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_BASAL_METABOLIC_RATE);
      iUploaded =            c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_UPLOADED);
      
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
      value.put("bodyWeight", c.getDouble(iBodyWeight));
      if(!c.isNull(iMuscleMass))
        value.put("muscleMass", c.getDouble(iMuscleMass));
      if(!c.isNull(iBoneMass))
        value.put("boneMass", c.getDouble(iBoneMass));
      if(!c.isNull(iFatPercentage))
        value.put("fatPercent", c.getDouble(iFatPercentage));
      if(!c.isNull(iHydrationPercentage))
        value.put("hydraPercent", c.getDouble(iHydrationPercentage));
      if(!c.isNull(iActiveMetRate))
        value.put("activeMetRate", c.getDouble(iActiveMetRate));
      if(!c.isNull(iBasalMetRate))
        value.put("basalMetRate", c.getDouble(iBasalMetRate));
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

  

  public static final Parcelable.Creator<WSMeasurementDbUploadHelper> CREATOR = new Creator<WSMeasurementDbUploadHelper>() {
    @Override
    public WSMeasurementDbUploadHelper[] newArray(int size) {
      return new WSMeasurementDbUploadHelper[size];
    }
    @Override
    public WSMeasurementDbUploadHelper createFromParcel(Parcel source) {
      return new WSMeasurementDbUploadHelper(source);
    }
  };
}
