package cz.zcu.kiv.mobile.logger.data.database.commands;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.GlucoseMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.glucose.GlucoseMeasurement;


public class InsertGlucoseMeasurementCommand extends AInsertMeasurementCommand<GlucoseMeasurement> {
  protected static final GlucoseMeasurementTable dbGM = Application.getInstance().getDatabase().getGlucoseMeasurementTable();

  
  public InsertGlucoseMeasurementCommand(long userID, GlucoseMeasurement measurement, InsertCommandListener listener) {
    super(userID, measurement, listener);
  }


  @Override
  protected long insertToDatabase(long userID, GlucoseMeasurement measurement) throws DatabaseException {
    return dbGM.addMeasurement(userID, measurement);
  }
}
