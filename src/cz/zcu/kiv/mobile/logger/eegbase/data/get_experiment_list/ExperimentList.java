package cz.zcu.kiv.mobile.logger.eegbase.data.get_experiment_list;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.os.Parcel;
import android.os.Parcelable;


@Root(name = "experiments")
public class ExperimentList implements Parcelable {
  @ElementList(inline=true)
  private List<Experiment> experiments;

  
  public ExperimentList() {
    this(new ArrayList<Experiment>());
  }

  public ExperimentList(List<Experiment> experiments) {
    this.experiments = experiments;
  }
  
  public ExperimentList(Parcel source) {
    this();
    readFromParcel(source);
  }
  
  
  public List<Experiment> getExperiments() {
    return experiments;
  }
  
  public void setExperiments(List<Experiment> experiments) {
    this.experiments = experiments;
  }
  
  
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeTypedList(experiments);
  }

  public void readFromParcel(Parcel source) {
    source.readTypedList(experiments, Experiment.CREATOR);
  }


  public static final Parcelable.Creator<ExperimentList> CREATOR = new Creator<ExperimentList>() {
    @Override
    public ExperimentList[] newArray(int size) {
      return new ExperimentList[size];
    }
    @Override
    public ExperimentList createFromParcel(Parcel source) {
      return new ExperimentList(source);
    }
  };
}
