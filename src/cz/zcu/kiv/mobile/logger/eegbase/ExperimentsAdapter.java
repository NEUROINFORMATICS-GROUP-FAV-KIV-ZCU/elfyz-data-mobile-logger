package cz.zcu.kiv.mobile.logger.eegbase;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.eegbase.data.get_experiment_list.Experiment;


public class ExperimentsAdapter extends BaseAdapter {
  private List<Experiment> experiments;

  private LayoutInflater inflater;
  
  
  public ExperimentsAdapter(Context context) {
    inflater = LayoutInflater.from(context);
  }
  
  
  public void setExperiments(List<Experiment> experiments) {
    this.experiments = experiments;
    notifyDataSetChanged();
  }
  
  public List<Experiment> getExperiments() {
    return experiments;
  }
  
  public Experiment getExperiment(int position) {
    return experiments.get(position);
  }

  @Override
  public int getCount() {
    return experiments == null ? 0 : experiments.size();
  }

  @Override
  public Object getItem(int position) {
    return experiments.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder = null;
    
    if(convertView == null) {
      convertView = inflater.inflate(R.layout.row_experiment, null);
      
      holder = new ViewHolder();
      setViews(holder, convertView);
      
      convertView.setTag(holder);
    }
    else {
      holder = (ViewHolder) convertView.getTag();
    }
    
    setData(holder, experiments.get(position));
    
    return convertView;
  }
  
  private void setData(ViewHolder holder, Experiment experiment) {
    holder.tvExperimentID.setText(experiment.getExperimentId());
    holder.tvScenarioName.setText(experiment.getScenarioName());
    holder.tvResearchGroupName.setText(experiment.getResearchGroupName());
    holder.tvTimeFrom.setText(experiment.getStartTime());
    holder.tvTimeTo.setText(experiment.getEndTime());
    holder.tvSubjectName.setText(experiment.getSubjectName());
    holder.tvSubjectSurname.setText(experiment.getSubjectSurname());
  }

  private void setViews(ViewHolder holder, View view) {
    holder.tvExperimentID =      (TextView) view.findViewById(R.id.tv_experiment_id);
    holder.tvScenarioName =      (TextView) view.findViewById(R.id.tv_scenario_name);
    holder.tvResearchGroupName = (TextView) view.findViewById(R.id.tv_research_group_name);
    holder.tvTimeFrom =          (TextView) view.findViewById(R.id.tv_time_from);
    holder.tvTimeTo =            (TextView) view.findViewById(R.id.tv_time_to);
    holder.tvSubjectName =       (TextView) view.findViewById(R.id.tv_subject_name);
    holder.tvSubjectSurname =    (TextView) view.findViewById(R.id.tv_subject_surname);
  }


  static class ViewHolder {
    TextView tvExperimentID;
    TextView tvScenarioName;
    TextView tvResearchGroupName;
    TextView tvTimeFrom;
    TextView tvTimeTo;
    TextView tvSubjectName;
    TextView tvSubjectSurname;
  }
}
