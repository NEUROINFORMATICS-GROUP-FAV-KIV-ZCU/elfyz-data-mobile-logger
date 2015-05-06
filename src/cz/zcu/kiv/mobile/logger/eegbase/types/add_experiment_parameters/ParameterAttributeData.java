package cz.zcu.kiv.mobile.logger.eegbase.types.add_experiment_parameters;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

import android.os.Parcel;
import android.os.Parcelable;


@Order(elements = {"name", "value"})
@Root(name = "parameterAttribute")
public class ParameterAttributeData implements Parcelable {

  @Element
  private String name;
  @Element
  private String value;


  public ParameterAttributeData() {
  }

  public ParameterAttributeData(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public ParameterAttributeData(Parcel source) {
    readFromParcel(source);
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
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
    if(value == null) {
      throw new NullPointerException("value should not be null");
    }
    dest.writeString(value);
  }

  public void readFromParcel(Parcel source) {
    name = source.readString();
    value = source.readString();
  }


  public static final Parcelable.Creator<ParameterAttributeData> CREATOR = new Creator<ParameterAttributeData>() {
    @Override
    public ParameterAttributeData[] newArray(int size) {
      return new ParameterAttributeData[size];
    }
    @Override
    public ParameterAttributeData createFromParcel(Parcel source) {
      return new ParameterAttributeData(source);
    }
  };
}
