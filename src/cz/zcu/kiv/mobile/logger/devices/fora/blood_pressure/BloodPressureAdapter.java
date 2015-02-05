package cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.database.BloodPressureMeasurementTable;


public class BloodPressureAdapter extends CursorAdapter {
  protected LayoutInflater inflater;
  protected DateFormat timeFormat;
  
  protected String systolic_;
  protected String diastolic_;
  protected String mean_;
  protected String heartBeat_;

  protected int iTime;
  protected int iSystolic;
  protected int iDiastolic;
  protected int iMeanPressure;
  protected int iHeartRate;


  public BloodPressureAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
    inflater = LayoutInflater.from(context);
    timeFormat = SimpleDateFormat.getDateTimeInstance();
    if(c != null) {
      getIndices(c);
    }
    systolic_ = context.getString(R.string.blood_pressure_short_systolic_);
    diastolic_ = context.getString(R.string.blood_pressure_short_diastolic_);
    mean_ = context.getString(R.string.blood_pressure_short_mean_);
    heartBeat_ = context.getString(R.string.blood_pressure_short_heart_beat_);
  }


  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    ViewHolder holder = (ViewHolder) view.getTag();

    holder.tvTime.setText(timeFormat.format(cursor.getLong(iTime)));
    holder.tvSystolic.setText(systolic_ + String.valueOf(cursor.getInt(iSystolic)));
    holder.tvDiastolic.setText(diastolic_ + String.valueOf(cursor.getInt(iDiastolic)));
    holder.tvMeanPressure.setText(mean_ + String.valueOf(cursor.getInt(iMeanPressure)));
    holder.tvHeartRate.setText(heartBeat_ + String.valueOf(cursor.getInt(iHeartRate)));
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = inflater.inflate(R.layout.blood_pressure_row, parent, false);

    ViewHolder holder = new ViewHolder();
    holder.tvTime =         (TextView) view.findViewById(R.id.tv_time);
    holder.tvSystolic =     (TextView) view.findViewById(R.id.tv_systolic);
    holder.tvDiastolic =    (TextView) view.findViewById(R.id.tv_diastolic);
    holder.tvMeanPressure = (TextView) view.findViewById(R.id.tv_mean_pressure);
    holder.tvHeartRate =    (TextView) view.findViewById(R.id.tv_heart_rate);
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
    iTime =         c.getColumnIndexOrThrow(BloodPressureMeasurementTable.COLUMN_TIME);
    iSystolic =     c.getColumnIndexOrThrow(BloodPressureMeasurementTable.COLUMN_SYSTOLIC);
    iDiastolic =    c.getColumnIndexOrThrow(BloodPressureMeasurementTable.COLUMN_DIASTOLIC);
    iMeanPressure = c.getColumnIndexOrThrow(BloodPressureMeasurementTable.COLUMN_MEAN_PRESSURE);
    iHeartRate =    c.getColumnIndexOrThrow(BloodPressureMeasurementTable.COLUMN_HEART_RATE);
  }


  static class ViewHolder {
    TextView tvTime;
    TextView tvSystolic;
    TextView tvDiastolic;
    TextView tvMeanPressure;
    TextView tvHeartRate;
  }
}
