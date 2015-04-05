package cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name = "addExperimentDataResult")
public class AddExperimentDataResult {
  @Element
  private boolean success;

  
  public AddExperimentDataResult() {}

  public AddExperimentDataResult(boolean success) {
    this.success = success;
  }

  
  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }
}
