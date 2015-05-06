package cz.zcu.kiv.mobile.logger.eegbase.types.add_experiment_parameters;


import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.os.Parcel;
import android.os.Parcelable;


@Root(name = "parameterAttributes")
public class ParameterAttributeDataList implements Parcelable {
  @ElementList(inline=true)
  private List<ParameterAttributeData> parameterAttributes;


  public ParameterAttributeDataList() {
    this(new ArrayList<ParameterAttributeData>());
  }

  public ParameterAttributeDataList(List<ParameterAttributeData> parameterAttributes) {
    this.parameterAttributes = parameterAttributes;
  }

  public ParameterAttributeDataList(Parcel source) {
    this();
    readFromParcel(source);
  }


  public List<ParameterAttributeData> getParameterAttributes() {
    return parameterAttributes;
  }

  public void setParameterAttributes(List<ParameterAttributeData> parameterAttributes) {
    this.parameterAttributes = parameterAttributes;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeTypedList(parameterAttributes);
  }

  public void readFromParcel(Parcel source) {
    source.readTypedList(parameterAttributes, ParameterAttributeData.CREATOR);
  }


  public static final Parcelable.Creator<ParameterAttributeDataList> CREATOR = new Creator<ParameterAttributeDataList>() {
    @Override
    public ParameterAttributeDataList[] newArray(int size) {
      return new ParameterAttributeDataList[size];
    }
    @Override
    public ParameterAttributeDataList createFromParcel(Parcel source) {
      return new ParameterAttributeDataList(source);
    }
  };
}
