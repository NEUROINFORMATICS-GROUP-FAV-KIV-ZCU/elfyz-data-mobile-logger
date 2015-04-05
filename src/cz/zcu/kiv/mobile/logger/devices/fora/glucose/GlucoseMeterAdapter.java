package cz.zcu.kiv.mobile.logger.devices.fora.glucose;

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
import cz.zcu.kiv.mobile.logger.data.database.GlucoseMeasurementTable;


public class GlucoseMeterAdapter extends CursorAdapter {
  protected LayoutInflater inflater;
  protected DateFormat timeFormat;
  
  protected int iTime;
  protected int iGlucose;
  protected int iTemperature;
  protected int iCode;
  protected int iType;
  protected int iUploaded;


  public GlucoseMeterAdapter(Context context, Cursor c, int flags) {
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
    holder.tvGlucose.setText(String.valueOf(cursor.getInt(iGlucose)));
    holder.tvTemperature.setText(String.valueOf(cursor.getInt(iTemperature)));
    holder.tvCode.setText(String.valueOf(cursor.getInt(iCode)));
    holder.tvType.setText(String.valueOf(cursor.getInt(iType)));
    holder.ivUploaded.setVisibility(cursor.getInt(iUploaded) == ATable.VALUE_TRUE ? View.VISIBLE : View.INVISIBLE);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = inflater.inflate(R.layout.row_glucose, parent, false);

    ViewHolder holder = new ViewHolder();
    holder.tvTime =        (TextView) view.findViewById(R.id.tv_time);
    holder.tvGlucose =     (TextView) view.findViewById(R.id.tv_glucose);
    holder.tvTemperature = (TextView) view.findViewById(R.id.tv_temperature);
    holder.tvCode =        (TextView) view.findViewById(R.id.tv_code);
    holder.tvType =        (TextView) view.findViewById(R.id.tv_type);
    holder.ivUploaded =     (ImageView) view.findViewById(R.id.iv_uploaded_icon);
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
    iTime =        c.getColumnIndexOrThrow(GlucoseMeasurementTable.COLUMN_TIME);
    iGlucose =     c.getColumnIndexOrThrow(GlucoseMeasurementTable.COLUMN_GLUCOSE);
    iTemperature = c.getColumnIndexOrThrow(GlucoseMeasurementTable.COLUMN_TEMPERATURE);
    iCode =        c.getColumnIndexOrThrow(GlucoseMeasurementTable.COLUMN_CODE);
    iType =        c.getColumnIndexOrThrow(GlucoseMeasurementTable.COLUMN_TYPE);
    iUploaded =    c.getColumnIndexOrThrow(GlucoseMeasurementTable.COLUMN_UPLOADED);
  }


  static class ViewHolder {
    TextView tvTime;
    TextView tvGlucose;
    TextView tvTemperature;
    TextView tvCode;
    TextView tvType;
    ImageView ivUploaded;
  }
}
