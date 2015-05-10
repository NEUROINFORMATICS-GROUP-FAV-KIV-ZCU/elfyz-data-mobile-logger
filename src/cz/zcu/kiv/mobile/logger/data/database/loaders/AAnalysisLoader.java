package cz.zcu.kiv.mobile.logger.data.database.loaders;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;


public abstract class AAnalysisLoader extends AMeasurementListLoader {
  private Date dateFrom;
  private Date dateTo;


  public AAnalysisLoader(Context context, long profileID) {
    super(context, profileID);
  }

  
  @Override
  protected Cursor getMeasurements() throws DatabaseException {
    if(dateFrom == null || dateTo == null) {
      return loadData();
    }
    else {
      return loadData(dateFrom, dateTo);
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
  

  protected abstract Cursor loadData() throws DatabaseException;
  protected abstract Cursor loadData(Date dateFrom, Date dateTo) throws DatabaseException;
}
