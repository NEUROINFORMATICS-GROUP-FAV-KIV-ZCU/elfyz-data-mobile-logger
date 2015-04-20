package cz.zcu.kiv.mobile.logger.eegbase.upload_helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.ExperimentParametersData;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.GenericParameterData;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.GenericParameterDataList;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.ParameterAttributeData;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.ParameterAttributeDataList;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.utils.CloseUtils;
import android.database.Cursor;
import android.os.Parcel;


public abstract class ADbUploadHelper implements IExperimentParametersUploadHelper {
  protected long[] ids;
  protected boolean append = false;
  protected String parameterName;
  protected String valueString;
  protected Double valueInteger;


  public ADbUploadHelper() { }
  
  public ADbUploadHelper(String parameterName, String valueString, long[] ids, boolean append) {
    this.parameterName = parameterName;
    this.valueString = valueString;
    this.ids = ids;
    this.append = append;
  }
  
  public ADbUploadHelper(String parameterName, Double valueInteger, long[] ids, boolean append) {
    this.parameterName = parameterName;
    this.valueInteger = valueInteger;
    this.ids = ids;
    this.append = append;
  }
  
  public ADbUploadHelper(Parcel source) {
    readFromParcel(source);
  }

  
  @Override
  public ExperimentParametersData buildParameters() throws UploadHelperException {
    Cursor data = null;
    try {
      data = init();
      ExperimentParametersData parameters = assembleParameters(data);
      return parameters;
    }
    finally{
      CloseUtils.close(data);
    }
  }
  
  protected ExperimentParametersData assembleParameters(Cursor data) throws UploadHelperException {
    try {
      List<ParameterAttributeData> attributes = new ArrayList<ParameterAttributeData>();

      while(data.moveToNext()) {
        attributes.add(
            new ParameterAttributeData(
                getID(data),
                toJSON(data)));
      }

      GenericParameterData parameter =
          (valueString != null)
            ? new GenericParameterData(parameterName, valueString, new ParameterAttributeDataList(attributes))
            : new GenericParameterData(parameterName, valueInteger, new ParameterAttributeDataList(attributes));

      return
          new ExperimentParametersData(
              append, 
              new GenericParameterDataList(
                  Arrays.asList(parameter)));
    }
    catch (JSONException e) {
      throw new UploadHelperException("Failed to build parameters.", e);
    }
  }

  
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(ids.length);
    dest.writeLongArray(ids);
    dest.writeInt(append ? 1 : 0);
    dest.writeString(parameterName);
    if(valueString == null) {
      dest.writeInt(0);
    }
    else {
      dest.writeInt(1);
      dest.writeString(valueString);
    }
    if(valueInteger == null) {
      dest.writeInt(0);
    }
    else {
      dest.writeInt(1);
      dest.writeDouble(valueInteger);
    }
  }

  public void readFromParcel(Parcel source) {
    ids = new long[source.readInt()];
    source.readLongArray(ids);
    append = source.readInt() == 1;
    parameterName = source.readString();
    if(source.readInt() == 1) {
      valueString = source.readString();
    }
    if(source.readInt() == 1) {
      valueInteger = source.readDouble();
    }
  }


  @Override
  public String getDisplayableInfo() {
    return parameterName + " - " + ids.length + Application.getStringResource(R.string.records);
  }


  protected abstract Cursor init() throws UploadHelperException;
  protected abstract String getID(Cursor data);
  protected abstract String toJSON(Cursor c) throws JSONException;
}
