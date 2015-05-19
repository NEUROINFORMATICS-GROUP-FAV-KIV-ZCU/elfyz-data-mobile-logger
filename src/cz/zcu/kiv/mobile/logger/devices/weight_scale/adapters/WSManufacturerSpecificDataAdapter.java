package cz.zcu.kiv.mobile.logger.devices.weight_scale.adapters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.database.ATable;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleManufacturerSpecificDataTable;


public class WSManufacturerSpecificDataAdapter extends CursorAdapter {
  private static final int PREVIEW_LENGTH = 4;
  
  protected LayoutInflater inflater;
  protected DateFormat timeFormat;

  protected int iTime;
  protected int iManufSpecific;
  protected int iUploaded;
  

  public WSManufacturerSpecificDataAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
    inflater = LayoutInflater.from(context);
    timeFormat = SimpleDateFormat.getDateTimeInstance();
    if(c != null) {
      getIndices(c);
    }
  }


  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    ViewHolder holder = (ViewHolder) view.getTag();
    
    byte[] data = cursor.getBlob(iManufSpecific);
    
    holder.tvTime.setText(timeFormat.format(cursor.getLong(iTime)));
    holder.tvManufSpecific.setText(String.valueOf(getPreview(data)));
    holder.tvDataLength.setText("Len " + String.valueOf(data.length));
    holder.ivUploaded.setVisibility(cursor.getInt(iUploaded) == ATable.VALUE_TRUE ? View.VISIBLE : View.INVISIBLE);
  }


  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = inflater.inflate(R.layout.row_ws_manufacturer_specific, parent, false);

    ViewHolder holder = new ViewHolder();
    holder.tvTime =          (TextView) view.findViewById(R.id.tv_time);
    holder.tvManufSpecific = (TextView) view.findViewById(R.id.tv_man_spec_byte);
    holder.tvDataLength =    (TextView) view.findViewById(R.id.tv_data_length);
    holder.ivUploaded =      (ImageView) view.findViewById(R.id.iv_uploaded_icon);
    view.setTag(holder);

    return view;
  }


  @Override
  public Cursor swapCursor(Cursor newCursor) {
    if(newCursor != null) {
      getIndices(newCursor);
    }
    return super.swapCursor(newCursor);
  }

  private void getIndices(Cursor c) {
    iTime =          c.getColumnIndexOrThrow(WeightScaleManufacturerSpecificDataTable.COLUMN_TIME);
    iManufSpecific = c.getColumnIndexOrThrow(WeightScaleManufacturerSpecificDataTable.COLUMN_DATA);
    iUploaded =      c.getColumnIndexOrThrow(WeightScaleManufacturerSpecificDataTable.COLUMN_UPLOADED);
  }

  private String getPreview(byte[] data) {
    StringBuilder sb = new StringBuilder();
    int length = data.length > PREVIEW_LENGTH ? PREVIEW_LENGTH : data.length;
    
    for (int i = 0; i < length; i++) {
      sb.append(String.format("%02X ", data[i]));
    }
    
    if(data.length > PREVIEW_LENGTH)
      sb.append("...");
    
    return sb.toString();
  }


  static class ViewHolder {
    TextView tvTime;
    TextView tvManufSpecific;
    TextView tvDataLength;
    ImageView ivUploaded;
  }
}
