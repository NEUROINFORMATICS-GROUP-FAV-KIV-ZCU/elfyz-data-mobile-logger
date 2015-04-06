package cz.zcu.kiv.mobile.logger.eegbase;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.ExperimentParametersData;
import cz.zcu.kiv.mobile.logger.eegbase.tasks.UploadGenericParametersTask;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.IExperimentParametersUploadHelper;
import cz.zcu.kiv.mobile.logger.utils.AndroidUtils;


public class UploadGenericParametersActivity extends Activity {
  public static final String PARAM_GENERIC_PARAMETERS = "generic.parameters";
  public static final String PARAM_GENERIC_PARAMETERS_UPLOAD_HELPER = "generis.parameters.upload.helper";
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_upload_generic_parameters);
    
    //upload parameters
    ExperimentParametersData data = getIntent().getParcelableExtra(PARAM_GENERIC_PARAMETERS);
    if(data != null) {
      new UploadGenericParametersTask(data).execute();
      return;
    }
    
    //use helper to upload parameters
    IExperimentParametersUploadHelper uploadHelper = getIntent().getParcelableExtra(PARAM_GENERIC_PARAMETERS_UPLOAD_HELPER);
    if(uploadHelper != null) {
      new UploadGenericParametersTask(uploadHelper).execute();
      return;
    }
    
    AndroidUtils.toast(this, R.string.fail_no_upload_data);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.upload_data, menu);
    return true;
  }
}
