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
import cz.zcu.kiv.mobile.logger.data.database.HeartRateCalculatedRrIntervalTable;


public class HRCalculatedRrIntervalAdapter extends CursorAdapter {
  protected LayoutInflater inflater;
  protected DateFormat timeFormat;

  protected int iTime;
  protected int iRrFlag;
  protected int iRrInterval;
  protected int iUploaded;
  

  public HRCalculatedRrIntervalAdapter(Context context, Cursor c, int flags) {
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
    holder.tvRrFlag.setText(HeartRateCalculatedRrIntervalTable.mapRrFlag(cursor.getInt(iRrFlag)).toString());
    holder.tvRrInterval.setText(String.valueOf(cursor.getInt(iRrInterval)) + " ms");
    holder.ivUploaded.setVisibility(cursor.getInt(iUploaded) == ATable.VALUE_TRUE ? View.VISIBLE : View.INVISIBLE);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = inflater.inflate(R.layout.row_hr_calculated_rr_interval, parent, false);

    ViewHolder holder = new ViewHolder();
    holder.tvTime =       (TextView) view.findViewById(R.id.tv_time);
    holder.tvRrFlag =     (TextView) view.findViewById(R.id.tv_rr_flag);
    holder.tvRrInterval = (TextView) view.findViewById(R.id.tv_rr_interval);
    holder.ivUploaded =   (ImageView) view.findViewById(R.id.iv_uploaded_icon);
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
    iTime =       c.getColumnIndexOrThrow(HeartRateCalculatedRrIntervalTable.COLUMN_TIME);
    iRrFlag =     c.getColumnIndexOrThrow(HeartRateCalculatedRrIntervalTable.COLUMN_RR_FLAG);
    iRrInterval = c.getColumnIndexOrThrow(HeartRateCalculatedRrIntervalTable.COLUMN_CALC_RR_INTERVAL);
    iUploaded =   c.getColumnIndexOrThrow(HeartRateCalculatedRrIntervalTable.COLUMN_UPLOADED);
  }
  
  
  static class ViewHolder {
    TextView tvTime;
    TextView tvRrFlag;
    TextView tvRrInterval;
    ImageView ivUploaded;
  }
}
