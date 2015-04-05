package cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

import android.os.Parcel;
import android.os.Parcelable;


@Order(elements = {"append", "genericParameters"})
@Root(name = "experimentParameters")
public class ExperimentParametersData implements Parcelable {
  @Element
  private boolean append = false;
  @Element
  private GenericParameterDataList genericParameters;
  

  public ExperimentParametersData() {}

  public ExperimentParametersData(boolean append, GenericParameterDataList genericParameters) {
    this.append = append;
    this.genericParameters = genericParameters;
  }

  public ExperimentParametersData(Parcel source) {
    readFromParcel(source);
  }

  
  public boolean isAppend() {
    return append;
  }

  public void setAppend(boolean append) {
    this.append = append;
  }

  public GenericParameterDataList getGenericParameters() {
    return genericParameters;
  }

  public void setGenericParameters(GenericParameterDataList genericParameters) {
    this.genericParameters = genericParameters;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(append ? 1 : 0);
    if(genericParameters == null) {
      throw new NullPointerException("genericParameters should not be null");
    }
    dest.writeParcelable(genericParameters, flags);
  }

  public void readFromParcel(Parcel source) {
    append = source.readInt() == 1;
    genericParameters = source.readParcelable(GenericParameterDataList.class.getClassLoader());
  }


  public static final Parcelable.Creator<ExperimentParametersData> CREATOR = new Creator<ExperimentParametersData>() {
    @Override
    public ExperimentParametersData[] newArray(int size) {
      return new ExperimentParametersData[size];
    }
    @Override
    public ExperimentParametersData createFromParcel(Parcel source) {
      return new ExperimentParametersData(source);
    }
  };
}
