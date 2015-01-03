package cz.zcu.kiv.mobile.logger.ant.picker;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.AsyncScanResultDeviceInfo;


public class AntDeviceAdapter extends BaseAdapter {
  protected LayoutInflater inflater;
  protected List<AsyncScanResultDeviceInfo> devices;
  
  
  public AntDeviceAdapter(Context context) {
    inflater = LayoutInflater.from(context);
    devices = new ArrayList<AsyncScanResultDeviceInfo>();
  }
  

  @Override
  public int getCount() {
    return devices.size();
  }

  @Override
  public AsyncScanResultDeviceInfo getItem(int position) {
    return devices.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  public void clear() {
    devices.clear();
    notifyDataSetChanged();
  }


  public void add(AsyncScanResultDeviceInfo device) {
    devices.add(device);
    notifyDataSetChanged();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view;
    ViewHolder holder;
    
    if(convertView == null) {
      view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
      holder = new ViewHolder();
      holder.text = (TextView) view.findViewById(android.R.id.text1);
      view.setTag(holder);
    }
    else {
      view = convertView;
      holder = (ViewHolder) view.getTag();
    }
    
    AsyncScanResultDeviceInfo device = devices.get(position);
    holder.text.setText(device.getDeviceDisplayName());
    
    return view;
  }
  

  
  static class ViewHolder {
    TextView text;
  }
}
