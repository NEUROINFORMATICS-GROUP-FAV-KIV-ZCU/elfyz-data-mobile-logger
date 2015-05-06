package cz.zcu.kiv.mobile.logger.eegbase.types.add_experiment_parameters;


import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.os.Parcel;
import android.os.Parcelable;


@Root(name = "genericParameters")
public class GenericParameterDataList implements Parcelable {
  @ElementList(inline=true)
  private List<GenericParameterData> genericParameters;
  

  public GenericParameterDataList() {
    this(new ArrayList<GenericParameterData>());
  }

  public GenericParameterDataList(List<GenericParameterData> genericParameters) {
    this.genericParameters = genericParameters;
  }

  public GenericParameterDataList(Parcel source) {
    this();
    readFromParcel(source);
  }

  public List<GenericParameterData> getGenericParameters() {
    return genericParameters;
  }

  public void setGenericParameters(List<GenericParameterData> genericParameters) {
    this.genericParameters = genericParameters;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeTypedList(genericParameters);
  }

  public void readFromParcel(Parcel source) {
    source.readTypedList(genericParameters, GenericParameterData.CREATOR);
  }


  public static final Parcelable.Creator<GenericParameterDataList> CREATOR = new Creator<GenericParameterDataList>() {
    @Override
    public GenericParameterDataList[] newArray(int size) {
      return new GenericParameterDataList[size];
    }
    @Override
    public GenericParameterDataList createFromParcel(Parcel source) {
      return new GenericParameterDataList(source);
    }
  };
}
