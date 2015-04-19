package cz.zcu.kiv.mobile.logger.retain;

import java.util.List;

import cz.zcu.kiv.mobile.logger.eegbase.data.get_experiment_list.Experiment;

import android.app.Fragment;
import android.os.Bundle;


public class ExperimentRetainFragment extends Fragment {
  private List<Experiment> experiments;
  
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }
  
  
  public void setExperiments(List<Experiment> experiments) {
    this.experiments = experiments;
  }
  
  public List<Experiment> getExperiments() {
    return experiments;
  }
}