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
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleBatteryStatusTable;


public class WSBatteryStatusAdapter extends CursorAdapter {
  protected LayoutInflater inflater;
  protected DateFormat timeFormat;

  protected int iTime;
  protected int iCumulOpTime;
  protected int iCumulOpTimeRes;
  protected int iBatteryVoltage;
  protected int iBatteryStatus;
  protected int iBatteryCount;
  protected int iBatteryId;
  protected int iUploaded;
  

  public WSBatteryStatusAdapter(Context context, Cursor c, int flags) {
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
    holder.tvCumulOpTime.setText(String.valueOf(cursor.getLong(iCumulOpTime)));
    holder.tvCumulOpTimeRes.setText(String.valueOf(cursor.getInt(iCumulOpTimeRes)));
    holder.tvBatteryVoltage.setText(String.valueOf(cursor.getDouble(iBatteryVoltage)));
    holder.tvBatteryStatus.setText(String.valueOf(WeightScaleBatteryStatusTable.mapBatteryStatus(cursor.getInt(iBatteryStatus))));
    holder.tvBatteryCount.setText(String.valueOf(cursor.getInt(iBatteryCount)));
    holder.tvBatteryId.setText(String.valueOf(cursor.getInt(iBatteryId)));
    holder.ivUploaded.setVisibility(cursor.getInt(iUploaded) == ATable.VALUE_TRUE ? View.VISIBLE : View.INVISIBLE);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = inflater.inflate(R.layout.row_ws_battery_status, parent, false);

    ViewHolder holder = new ViewHolder();
    holder.tvTime =           (TextView) view.findViewById(R.id.tv_time);
    holder.tvCumulOpTime =    (TextView) view.findViewById(R.id.tv_cumul_op_time);
    holder.tvCumulOpTimeRes = (TextView) view.findViewById(R.id.tv_cumul_op_time_res);
    holder.tvBatteryVoltage = (TextView) view.findViewById(R.id.tv_battery_voltage);
    holder.tvBatteryStatus =  (TextView) view.findViewById(R.id.tv_battery_status);
    holder.tvBatteryCount =   (TextView) view.findViewById(R.id.tv_battery_count);
    holder.tvBatteryId =      (TextView) view.findViewById(R.id.tv_battery_id);
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
    iTime =           c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_TIME);
    iCumulOpTime =    c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_CUMUL_OP_TIME);
    iCumulOpTimeRes = c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_CUMUL_OP_TIME_RES);
    iBatteryVoltage = c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_BAT_VOLTAGE);
    iBatteryStatus =  c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_BAT_STATUS);
    iBatteryCount =   c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_BAT_COUNT);
    iBatteryId =      c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_BAT_ID);
    iUploaded =       c.getColumnIndexOrThrow(WeightScaleBatteryStatusTable.COLUMN_UPLOADED);
  }


  static class ViewHolder {
    TextView tvTime;
    TextView tvCumulOpTime;
    TextView tvCumulOpTimeRes;
    TextView tvBatteryVoltage;
    TextView tvBatteryStatus;
    TextView tvBatteryCount;
    TextView tvBatteryId;
    ImageView ivUploaded;
  }
}
