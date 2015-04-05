package cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

import android.os.Parcel;
import android.os.Parcelable;


@Order(elements = {"name", "valueString", "valueInteger", "parameterAttributes"})
@Root(name = "genericParameter")
public class GenericParameterData implements Parcelable {

  @Element
  private String name;
  @Element(required=false)
  private String valueString;
  @Element(required=false)
  private Double valueInteger;
  @Element
  private ParameterAttributeDataList parameterAttributes;


  public GenericParameterData() {
  }

  public GenericParameterData(String name, Double valueInteger) {
    this.name = name;
    this.valueInteger = valueInteger;
  }

  public GenericParameterData(String name, String valueString) {
    this.name = name;
    this.valueString = valueString;
  }

  public GenericParameterData(String name, String valueString, ParameterAttributeDataList parameterAttributes) {
    this.name = name;
    this.valueString = valueString;
    this.parameterAttributes = parameterAttributes;
  }

  public GenericParameterData(String name, Double valueInteger, ParameterAttributeDataList parameterAttributes) {
    this.name = name;
    this.valueInteger = valueInteger;
    this.parameterAttributes = parameterAttributes;
  }

  public GenericParameterData(Parcel source) {
    readFromParcel(source);
  }

  
  public String getName() {
    return name;
  }

  public GenericParameterData setName(String name) {
    this.name = name;
    return this;
  }

  public String getValueString() {
    return valueString;
  }

  public GenericParameterData setValueString(String valueString) {
    this.valueString = valueString;
    return this;
  }

  public Double getValueInteger() {
    return valueInteger;
  }

  public GenericParameterData setValueInteger(Double valueInteger) {
    this.valueInteger = valueInteger;
    return this;
  }

  public ParameterAttributeDataList getParameterAttributes() {
    return parameterAttributes;
  }

  public GenericParameterData setParameterAttributes(ParameterAttributeDataList parameterAttributes) {
    this.parameterAttributes = parameterAttributes;
    return this;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    if(name == null) {
      throw new NullPointerException("name should not be null");
    }
    dest.writeString(name);
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
    if(parameterAttributes == null) {
      throw new NullPointerException("parameterAttributes should not be null");
    }
    dest.writeParcelable(parameterAttributes, flags);
  }

  public void readFromParcel(Parcel source) {
    name = source.readString();
    if(source.readInt() == 1) {
      valueString = source.readString();
    }
    if(source.readInt() == 1) {
      valueInteger = source.readDouble();
    }
    parameterAttributes = source.readParcelable(ParameterAttributeDataList.class.getClassLoader());
  }


  public static final Parcelable.Creator<GenericParameterData> CREATOR = new Creator<GenericParameterData>() {
    @Override
    public GenericParameterData[] newArray(int size) {
      return new GenericParameterData[size];
    }
    @Override
    public GenericParameterData createFromParcel(Parcel source) {
      return new GenericParameterData(source);
    }
  };
}
