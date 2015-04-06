package cz.zcu.kiv.mobile.logger.eegbase.data.get_experiment_list;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.os.Parcel;
import android.os.Parcelable;


@Root(name="experiment")
public class Experiment implements Parcelable {
  @Element
  private String experimentId;
  @Element
  private String scenarioName;
  @Element
  private String researchGroupName;
  @Element
  private String startTime;
  @Element
  private String endTime;
  @Element
  private String subjectName;
  @Element
  private String subjectSurname;
  
  
  public Experiment() { }

  public Experiment(String experimentId, String scenarioName, String researchGroupName,
      String startTime, String endTime, String subjectName, String subjectSurname) {
    this.experimentId = experimentId;
    this.scenarioName = scenarioName;
    this.researchGroupName = researchGroupName;
    this.startTime = startTime;
    this.endTime = endTime;
    this.subjectName = subjectName;
    this.subjectSurname = subjectSurname;
  }

  public Experiment(Parcel source) {
    readFromParcel(source);
  }

  
  public String getExperimentId() {
    return experimentId;
  }

  public void setExperimentId(String experimentId) {
    this.experimentId = experimentId;
  }

  public String getScenarioName() {
    return scenarioName;
  }

  public void setScenarioName(String scenarioName) {
    this.scenarioName = scenarioName;
  }

  public String getResearchGroupName() {
    return researchGroupName;
  }

  public void setResearchGroupName(String researchGroupName) {
    this.researchGroupName = researchGroupName;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public String getSubjectName() {
    return subjectName;
  }

  public void setSubjectName(String subjectName) {
    this.subjectName = subjectName;
  }

  public String getSubjectSurname() {
    return subjectSurname;
  }

  public void setSubjectSurname(String subjectSurname) {
    this.subjectSurname = subjectSurname;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(experimentId);
    dest.writeString(scenarioName);
    dest.writeString(researchGroupName);
    dest.writeString(startTime);
    dest.writeString(endTime);
    dest.writeString(subjectName);
    dest.writeString(subjectSurname);
  }

  public void readFromParcel(Parcel source) {
    experimentId = source.readString();
    scenarioName = source.readString();
    researchGroupName = source.readString();
    startTime = source.readString();
    endTime = source.readString();
    subjectName = source.readString();
    subjectSurname = source.readString();
  }


  public static final Parcelable.Creator<Experiment> CREATOR = new Creator<Experiment>() {
    @Override
    public Experiment[] newArray(int size) {
      return new Experiment[size];
    }
    @Override
    public Experiment createFromParcel(Parcel source) {
      return new Experiment(source);
    }
  };
}
