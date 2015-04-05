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
import cz.zcu.kiv.mobile.logger.data.database.HeartRateMeasurementTable;


public class HRMeasurementAdapter extends CursorAdapter {
  protected LayoutInflater inflater;
  protected DateFormat timeFormat;

  protected int iTime;
  protected int iDataState;
  protected int iHeartRate;
  protected int iBeatCount;
  protected int iBeatTime;
  protected int iUploaded;
  

  public HRMeasurementAdapter(Context context, Cursor c, int flags) {
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
    holder.tvDataState.setText(HeartRateMeasurementTable.mapDataState(cursor.getInt(iDataState)).toString());
    holder.tvHeartRate.setText(String.valueOf(cursor.getInt(iHeartRate)));
    holder.tvBeatCount.setText(String.valueOf(cursor.getInt(iBeatCount)));
    holder.tvBeatTime.setText(String.valueOf(cursor.getInt(iBeatTime)));
    holder.ivUploaded.setVisibility(cursor.getInt(iUploaded) == ATable.VALUE_TRUE ? View.VISIBLE : View.INVISIBLE);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = inflater.inflate(R.layout.row_hr_measurement, parent, false);

    ViewHolder holder = new ViewHolder();
    holder.tvTime =      (TextView) view.findViewById(R.id.tv_time);
    holder.tvDataState = (TextView) view.findViewById(R.id.tv_data_state);
    holder.tvHeartRate = (TextView) view.findViewById(R.id.tv_heart_rate);
    holder.tvBeatCount = (TextView) view.findViewById(R.id.tv_beat_count);
    holder.tvBeatTime =  (TextView) view.findViewById(R.id.tv_beat_time);
    holder.ivUploaded =  (ImageView) view.findViewById(R.id.iv_uploaded_icon);
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
    iTime =      c.getColumnIndexOrThrow(HeartRateMeasurementTable.COLUMN_TIME);
    iDataState = c.getColumnIndexOrThrow(HeartRateMeasurementTable.COLUMN_DATA_STATE);
    iHeartRate = c.getColumnIndexOrThrow(HeartRateMeasurementTable.COLUMN_HEART_RATE);
    iBeatCount = c.getColumnIndexOrThrow(HeartRateMeasurementTable.COLUMN_BEAT_COUNT);
    iBeatTime =  c.getColumnIndexOrThrow(HeartRateMeasurementTable.COLUMN_BEAT_TIME);
    iUploaded =  c.getColumnIndexOrThrow(HeartRateMeasurementTable.COLUMN_UPLOADED);
  }
  
  
  static class ViewHolder {
    TextView tvTime;
    TextView tvDataState;
    TextView tvHeartRate;
    TextView tvBeatCount;
    TextView tvBeatTime;
    ImageView ivUploaded;
  }
}
