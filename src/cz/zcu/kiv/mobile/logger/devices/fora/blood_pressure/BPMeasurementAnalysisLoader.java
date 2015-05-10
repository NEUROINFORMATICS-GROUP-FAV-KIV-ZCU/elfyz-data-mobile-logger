package cz.zcu.kiv.mobile.logger.devices.fora.blood_pressure;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;


public class BPMeasurementAnalysisLoader extends BPMeasurementLoader {
  private Date dateFrom;
  private Date dateTo;


  public BPMeasurementAnalysisLoader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected Cursor getMeasurements() throws DatabaseException {
    if(dateFrom == null || dateTo == null) {
      return db.getBloodPressureMeasurementTable().getMeasurements(profileID, true);
    }
    else {
      return db.getBloodPressureMeasurementTable().getMeasurements(profileID, true, dateFrom, dateTo);
    }
  }


  public void triggerLoad(Date dateFrom, Date dateTo) {
    setDates(dateFrom, dateTo);
    onContentChanged();
  }

  public void setDates(Date dateFrom, Date dateTo) {
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
  }
}
