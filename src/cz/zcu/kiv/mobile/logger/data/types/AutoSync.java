package cz.zcu.kiv.mobile.logger.data.types;

import android.os.Parcel;
import android.os.Parcelable;


public class AutoSync implements Parcelable {
  private int tableID;
  private String experimentID;
  private String experimentName;
  
  
  public AutoSync() {}

  public AutoSync(int tableID, String experimentID, String experimentName) {
    super();
    this.tableID = tableID;
    this.experimentID = experimentID;
    this.experimentName = experimentName;
  }
  
  public AutoSync(Parcel source) {
    readFromParcel(source);
  }

  
  public int getTableID() {
    return tableID;
  }

  public void setTableID(int tableID) {
    this.tableID = tableID;
  }

  public String getExperimentID() {
    return experimentID;
  }

  public void setExperimentID(String experimentID) {
    this.experimentID = experimentID;
  }

  public String getExperimentName() {
    return experimentName;
  }

  public void setExperimentName(String experimentName) {
    this.experimentName = experimentName;
  }
  
  
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(tableID);
    dest.writeString(experimentID);
    dest.writeString(experimentName);
  }
  
  public void readFromParcel(Parcel source) {
    tableID = source.readInt();
    experimentID = source.readString();
    experimentName = source.readString();
  }
  
  
  public static final Parcelable.Creator<AutoSync> CREATOR = new Creator<AutoSync>() {
    @Override
    public AutoSync[] newArray(int size) {
      return new AutoSync[size];
    }
    @Override
    public AutoSync createFromParcel(Parcel source) {
      return new AutoSync(source);
    }
  };
}
