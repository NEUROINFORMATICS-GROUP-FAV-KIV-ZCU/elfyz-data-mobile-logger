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
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleManufacturerIdentificationTable;


public class WSManufacturerIdentificationAdapter extends CursorAdapter {
  protected LayoutInflater inflater;
  protected DateFormat timeFormat;

  protected int iTime;
  protected int iHwRev;
  protected int iManufacturerId;
  protected int iModelNumber;
  protected int iUploaded;
  

  public WSManufacturerIdentificationAdapter(Context context, Cursor c, int flags) {
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
    
    holder.tvTime.setText(timeFormat.format(cursor.getLong(iTime)));
    holder.tvHwRev.setText(String.valueOf(cursor.getInt(iHwRev)));
    holder.tvManufacturerId.setText(String.valueOf(cursor.getInt(iManufacturerId)));
    holder.tvModelNumber.setText(String.valueOf(cursor.getInt(iModelNumber)));
    holder.ivUploaded.setVisibility(cursor.getInt(iUploaded) == ATable.VALUE_TRUE ? View.VISIBLE : View.INVISIBLE);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = inflater.inflate(R.layout.row_ws_manufacturer_id, parent, false);

    ViewHolder holder = new ViewHolder();
    holder.tvTime =           (TextView) view.findViewById(R.id.tv_time);
    holder.tvHwRev =          (TextView) view.findViewById(R.id.tv_hw_rev);
    holder.tvManufacturerId = (TextView) view.findViewById(R.id.tv_manufacturer_id);
    holder.tvModelNumber =    (TextView) view.findViewById(R.id.tv_model_nr);
    holder.ivUploaded =       (ImageView) view.findViewById(R.id.iv_uploaded_icon);
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
    iTime =           c.getColumnIndexOrThrow(WeightScaleManufacturerIdentificationTable.COLUMN_TIME);
    iHwRev =          c.getColumnIndexOrThrow(WeightScaleManufacturerIdentificationTable.COLUMN_HW_REV);
    iManufacturerId = c.getColumnIndexOrThrow(WeightScaleManufacturerIdentificationTable.COLUMN_MANUF_ID);
    iModelNumber =    c.getColumnIndexOrThrow(WeightScaleManufacturerIdentificationTable.COLUMN_MODEL_NR);
    iUploaded =       c.getColumnIndexOrThrow(WeightScaleManufacturerIdentificationTable.COLUMN_UPLOADED);
  }
  
  
  static class ViewHolder {
    TextView tvTime;
    TextView tvHwRev;
    TextView tvManufacturerId;
    TextView tvModelNumber;
    ImageView ivUploaded;
  }
}
