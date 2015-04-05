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
import cz.zcu.kiv.mobile.logger.data.database.HeartRateVersionAndModelTable;


public class HRVersionAndModelAdapter extends CursorAdapter {
  protected LayoutInflater inflater;
  protected DateFormat timeFormat;

  protected int iTime;
  protected int iHW;
  protected int iSW;
  protected int iModelNr;
  protected int iUploaded;
  

  public HRVersionAndModelAdapter(Context context, Cursor c, int flags) {
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
    holder.tvHW.setText(String.valueOf(cursor.getInt(iHW)));
    holder.tvSW.setText(String.valueOf(cursor.getInt(iSW)));
    holder.tvModelNr.setText(String.valueOf(cursor.getInt(iModelNr)));
    holder.ivUploaded.setVisibility(cursor.getInt(iUploaded) == ATable.VALUE_TRUE ? View.VISIBLE : View.INVISIBLE);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = inflater.inflate(R.layout.row_hr_version_and_model, parent, false);

    ViewHolder holder = new ViewHolder();
    holder.tvTime =     (TextView) view.findViewById(R.id.tv_time);
    holder.tvHW =       (TextView) view.findViewById(R.id.tv_hw);
    holder.tvSW =       (TextView) view.findViewById(R.id.tv_sw);
    holder.tvModelNr =  (TextView) view.findViewById(R.id.tv_model_nr);
    holder.ivUploaded = (ImageView) view.findViewById(R.id.iv_uploaded_icon);
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
    iTime =     c.getColumnIndexOrThrow(HeartRateVersionAndModelTable.COLUMN_TIME);
    iHW =       c.getColumnIndexOrThrow(HeartRateVersionAndModelTable.COLUMN_HW_VERSION);
    iSW =       c.getColumnIndexOrThrow(HeartRateVersionAndModelTable.COLUMN_SW_VERSION);
    iModelNr =  c.getColumnIndexOrThrow(HeartRateVersionAndModelTable.COLUMN_MODEL_NUMBER);
    iUploaded = c.getColumnIndexOrThrow(HeartRateVersionAndModelTable.COLUMN_UPLOADED);
  }
  
  
  static class ViewHolder {
    TextView tvTime;
    TextView tvHW;
    TextView tvSW;
    TextView tvModelNr;
    ImageView ivUploaded;
  }
}
