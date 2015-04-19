package cz.zcu.kiv.mobile.logger.eegbase.upload_helpers;

import android.os.Parcelable;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.ExperimentParametersData;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;


public interface IExperimentParametersUploadHelper extends Parcelable {
  public ExperimentParametersData buildParameters() throws UploadHelperException;
  public void markUploaded() throws UploadHelperException;
  public String getDisplayableInfo();
}
