package cz.zcu.kiv.mobile.logger.devices.weight_scale.adapters;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.R;
import cz.zcu.kiv.mobile.logger.data.database.ATable;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleMeasurementTable;


public class WSMeasurementAdapter extends CursorAdapter {
  private static final DecimalFormat DF = new DecimalFormat("#.00");
  
  protected LayoutInflater inflater;
  protected DateFormat timeFormat;

  protected int iTime;
  protected int iBodyWeight;
  protected int iMuscleMass;
  protected int iBoneMass;
  protected int iFatPercentage;
  protected int iHydrationPercentage;
  protected int iActiveMetRate;
  protected int iBasalMetRate;
  protected int iUploaded;
  

  public WSMeasurementAdapter(Context context, Cursor c, int flags) {
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
    holder.tvBodyWeight.setText("Wei " + getStringValue(cursor, iBodyWeight));
    holder.tvMuscleMass.setText("Mus " + getStringValue(cursor, iMuscleMass));
    holder.tvBoneMass.setText("Bone " + getStringValue(cursor, iBoneMass));
    holder.tvFatPercentage.setText("Fat " + getStringValue(cursor, iFatPercentage));
    holder.tvHydrationPercentage.setText("Hyd " + getStringValue(cursor, iHydrationPercentage));
    holder.tvActiveMetRate.setText("AMR " + getStringValue(cursor, iActiveMetRate));
    holder.tvBasalMetRate.setText("BMR " + getStringValue(cursor, iBasalMetRate));
    holder.ivUploaded.setVisibility(cursor.getInt(iUploaded) == ATable.VALUE_TRUE ? View.VISIBLE : View.INVISIBLE);
  }

  private CharSequence getStringValue(Cursor cursor, int index) {
    return cursor.isNull(index)
        ? Application.getStringResource(R.string.value_n_a)
        : DF.format(cursor.getDouble(index));
  }


  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = inflater.inflate(R.layout.row_ws_measurement, parent, false);

    ViewHolder holder = new ViewHolder();
    holder.tvTime =                (TextView) view.findViewById(R.id.tv_time);
    holder.tvBodyWeight =          (TextView) view.findViewById(R.id.tv_body_weight);
    holder.tvMuscleMass =          (TextView) view.findViewById(R.id.tv_muscle_mass);
    holder.tvBoneMass =            (TextView) view.findViewById(R.id.tv_bone_mass);
    holder.tvFatPercentage =       (TextView) view.findViewById(R.id.tv_fat_percentage);
    holder.tvHydrationPercentage = (TextView) view.findViewById(R.id.tv_hydration_percentage);
    holder.tvActiveMetRate =       (TextView) view.findViewById(R.id.tv_active_metabolic_rate);
    holder.tvBasalMetRate =        (TextView) view.findViewById(R.id.tv_basal_metabolic_rate);
    holder.ivUploaded =            (ImageView) view.findViewById(R.id.iv_uploaded_icon);
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
    iTime =                c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_TIME);
    iBodyWeight =          c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_WEIGHT);
    iMuscleMass =          c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_MUSCLE_MASS);
    iBoneMass =            c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_BONE_MASS);
    iFatPercentage =       c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_FAT_PERCENTAGE);
    iHydrationPercentage = c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_HYDRATION_PERCENTAGE);
    iActiveMetRate =       c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_ACTIVE_METABOLIC_RATE);
    iBasalMetRate =        c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_BASAL_METABOLIC_RATE);
    iUploaded =            c.getColumnIndexOrThrow(WeightScaleMeasurementTable.COLUMN_UPLOADED);
  }
  
  
  static class ViewHolder {
    TextView tvTime;
    TextView tvBodyWeight;
    TextView tvMuscleMass;
    TextView tvBoneMass;
    TextView tvFatPercentage;
    TextView tvHydrationPercentage;
    TextView tvActiveMetRate;
    TextView tvBasalMetRate;
    ImageView ivUploaded;
  }
}
