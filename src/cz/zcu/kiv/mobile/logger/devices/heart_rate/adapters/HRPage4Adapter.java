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
import cz.zcu.kiv.mobile.logger.data.database.HeartRatePage4Table;


public class HRPage4Adapter extends CursorAdapter {
  protected LayoutInflater inflater;
  protected DateFormat timeFormat;

  protected int iTime;
  protected int iManufSpecByte;
  protected int iPrevBeat;
  protected int iUploaded;
  

  public HRPage4Adapter(Context context, Cursor c, int flags) {
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
    holder.tvManufSpecByte.setText(String.valueOf(cursor.getInt(iManufSpecByte)));
    holder.tvPrevBeat.setText(String.valueOf(cursor.getInt(iPrevBeat)));
    holder.ivUploaded.setVisibility(cursor.getInt(iUploaded) == ATable.VALUE_TRUE ? View.VISIBLE : View.INVISIBLE);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = inflater.inflate(R.layout.row_hr_page4, parent, false);

    ViewHolder holder = new ViewHolder();
    holder.tvTime =          (TextView) view.findViewById(R.id.tv_time);
    holder.tvManufSpecByte = (TextView) view.findViewById(R.id.tv_man_spec_byte);
    holder.tvPrevBeat =      (TextView) view.findViewById(R.id.tv_prev_beat);
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
    iTime =           c.getColumnIndexOrThrow(HeartRatePage4Table.COLUMN_TIME);
    iManufSpecByte =  c.getColumnIndexOrThrow(HeartRatePage4Table.COLUMN_MANUFACTURER_SPECIFIC);
    iPrevBeat =       c.getColumnIndexOrThrow(HeartRatePage4Table.COLUMN_PREVIOUS_HB_TIME);
    iUploaded =       c.getColumnIndexOrThrow(HeartRatePage4Table.COLUMN_UPLOADED);
  }
  
  
  static class ViewHolder {
    TextView tvTime;
    TextView tvManufSpecByte;
    TextView tvPrevBeat;
    ImageView ivUploaded;
  }
}
