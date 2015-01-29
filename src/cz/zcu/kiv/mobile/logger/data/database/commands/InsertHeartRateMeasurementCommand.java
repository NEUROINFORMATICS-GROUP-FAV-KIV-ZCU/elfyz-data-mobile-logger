package cz.zcu.kiv.mobile.logger.data.database.commands;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateMeasurement;


public class InsertHeartRateMeasurementCommand extends AInsertMeasurementCommand<HeartRateMeasurement> {
  protected static final HeartRateMeasurementTable dbHRM = Application.getInstance().getDatabase().getHeartRateMeasurementTable();


  public InsertHeartRateMeasurementCommand(long userID, HeartRateMeasurement measurement, InsertCommandListener listener) {
    super(userID, measurement, listener);
  }


  @Override
  protected long insertToDatabase(long userID, HeartRateMeasurement measurement) throws DatabaseException {
    return dbHRM.addMeasurement(userID, measurement);
  }
}
