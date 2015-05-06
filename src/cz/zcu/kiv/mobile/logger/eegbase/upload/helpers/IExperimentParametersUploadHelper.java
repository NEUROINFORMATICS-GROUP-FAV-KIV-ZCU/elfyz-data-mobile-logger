package cz.zcu.kiv.mobile.logger.eegbase.upload.helpers;

import android.os.Parcelable;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.eegbase.types.add_experiment_parameters.ExperimentParametersData;


public interface IExperimentParametersUploadHelper extends Parcelable {
  public ExperimentParametersData buildParameters() throws UploadHelperException;
  public void markUploaded() throws UploadHelperException;
  public String getDisplayableInfo();
}
