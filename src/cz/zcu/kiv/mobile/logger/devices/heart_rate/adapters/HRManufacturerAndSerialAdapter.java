package cz.zcu.kiv.mobile.logger.devices.heart_rate.adapters;

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
import cz.zcu.kiv.mobile.logger.data.database.HeartRateManufacturerAndSerialTable;


public class HRManufacturerAndSerialAdapter extends CursorAdapter {
  protected LayoutInflater inflater;
  protected DateFormat timeFormat;

  protected int iTime;
  protected int iManufacturerID;
  protected int iSerialNr;
  protected int iUploaded;
  

  public HRManufacturerAndSerialAdapter(Context context, Cursor c, int flags) {
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
    holder.tvManufacturerID.setText("ManID " + String.valueOf(cursor.getInt(iManufacturerID)));
    holder.tvSerialNr.setText("SerNr " + String.valueOf(cursor.getInt(iSerialNr)));
    holder.ivUploaded.setVisibility(cursor.getInt(iUploaded) == ATable.VALUE_TRUE ? View.VISIBLE : View.INVISIBLE);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = inflater.inflate(R.layout.row_hr_manufacturer_and_serial, parent, false);

    ViewHolder holder = new ViewHolder();
    holder.tvTime =           (TextView) view.findViewById(R.id.tv_time);
    holder.tvManufacturerID = (TextView) view.findViewById(R.id.tv_manufacturer_id);
    holder.tvSerialNr =       (TextView) view.findViewById(R.id.tv_serial_nr);
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
    iTime =           c.getColumnIndexOrThrow(HeartRateManufacturerAndSerialTable.COLUMN_TIME);
    iManufacturerID = c.getColumnIndexOrThrow(HeartRateManufacturerAndSerialTable.COLUMN_MANUFACTURER_ID);
    iSerialNr =       c.getColumnIndexOrThrow(HeartRateManufacturerAndSerialTable.COLUMN_SERIAL_NUMBER);
    iUploaded =       c.getColumnIndexOrThrow(HeartRateManufacturerAndSerialTable.COLUMN_UPLOADED);
  }
  
  
  static class ViewHolder {
    TextView tvTime;
    TextView tvManufacturerID;
    TextView tvSerialNr;
    ImageView ivUploaded;
  }
}
