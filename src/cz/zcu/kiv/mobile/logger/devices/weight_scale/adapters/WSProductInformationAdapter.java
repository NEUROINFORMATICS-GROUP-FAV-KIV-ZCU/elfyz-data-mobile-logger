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
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleProductInformationTable;


public class WSProductInformationAdapter extends CursorAdapter {
  protected LayoutInflater inflater;
  protected DateFormat timeFormat;

  protected int iTime;
  protected int iVersionMain;
  protected int iVersionSupp;
  protected int iSerialNr;
  protected int iUploaded;
  

  public WSProductInformationAdapter(Context context, Cursor c, int flags) {
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
    holder.tvVersionMain.setText(String.valueOf(cursor.getInt(iVersionMain)));
    holder.tvVersionSupp.setText(String.valueOf(cursor.getInt(iVersionSupp)));
    holder.tvSerialNr.setText(String.valueOf(cursor.getInt(iSerialNr)));
    holder.ivUploaded.setVisibility(cursor.getInt(iUploaded) == ATable.VALUE_TRUE ? View.VISIBLE : View.INVISIBLE);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = inflater.inflate(R.layout.row_ws_product_information, parent, false);

    ViewHolder holder = new ViewHolder();
    holder.tvTime =        (TextView) view.findViewById(R.id.tv_time);
    holder.tvVersionMain = (TextView) view.findViewById(R.id.tv_version_main);
    holder.tvVersionSupp = (TextView) view.findViewById(R.id.tv_version_supp);
    holder.tvSerialNr =    (TextView) view.findViewById(R.id.tv_serial_nr);
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
    iTime =        c.getColumnIndexOrThrow(WeightScaleProductInformationTable.COLUMN_TIME);
    iVersionMain = c.getColumnIndexOrThrow(WeightScaleProductInformationTable.COLUMN_MAIN_SW_REV);
    iVersionSupp = c.getColumnIndexOrThrow(WeightScaleProductInformationTable.COLUMN_SUPP_SW_REV);
    iSerialNr =    c.getColumnIndexOrThrow(WeightScaleProductInformationTable.COLUMN_SERIAL_NR);
    iUploaded =    c.getColumnIndexOrThrow(WeightScaleProductInformationTable.COLUMN_UPLOADED);
  }
  
  
  static class ViewHolder {
    TextView tvTime;
    TextView tvVersionMain;
    TextView tvVersionSupp;
    TextView tvSerialNr;
    ImageView ivUploaded;
  }
}
