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
import cz.zcu.kiv.mobile.logger.data.database.HeartRateCumulativeOperatingTimeTable;


public class HRCumulativeOperatingTimeAdapter extends CursorAdapter {
  protected LayoutInflater inflater;
  protected DateFormat timeFormat;

  protected int iTime;
  protected int iCumulOpTime;
  protected int iUploaded;
  

  public HRCumulativeOperatingTimeAdapter(Context context, Cursor c, int flags) {
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
    holder.tvCumulOpTime.setText(String.valueOf(cursor.getInt(iCumulOpTime)));
    holder.ivUploaded.setVisibility(cursor.getInt(iUploaded) == ATable.VALUE_TRUE ? View.VISIBLE : View.INVISIBLE);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = inflater.inflate(R.layout.row_hr_cumulative_op_time, parent, false);

    ViewHolder holder = new ViewHolder();
    holder.tvTime =        (TextView) view.findViewById(R.id.tv_time);
    holder.tvCumulOpTime = (TextView) view.findViewById(R.id.tv_cumul_op_time);
    holder.ivUploaded =    (ImageView) view.findViewById(R.id.iv_uploaded_icon);
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
    iTime =        c.getColumnIndexOrThrow(HeartRateCumulativeOperatingTimeTable.COLUMN_TIME);
    iCumulOpTime = c.getColumnIndexOrThrow(HeartRateCumulativeOperatingTimeTable.COLUMN_CUMUL_OP_TIME);
    iUploaded =    c.getColumnIndexOrThrow(HeartRateCumulativeOperatingTimeTable.COLUMN_UPLOADED);
  }
  
  
  static class ViewHolder {
    TextView tvTime;
    TextView tvCumulOpTime;
    ImageView ivUploaded;
  }
}
