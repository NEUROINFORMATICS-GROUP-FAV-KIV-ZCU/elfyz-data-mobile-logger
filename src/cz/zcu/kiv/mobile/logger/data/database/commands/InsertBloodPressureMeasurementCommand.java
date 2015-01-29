package cz.zcu.kiv.mobile.logger.data.database.commands;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.BloodPressureMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.blood_pressure.BloodPressureMeasurement;


public class InsertBloodPressureMeasurementCommand extends AInsertMeasurementCommand<BloodPressureMeasurement> {
  protected static final BloodPressureMeasurementTable dbBPM = Application.getInstance().getDatabase().getBloodPressureMeasurementTable();

  
  public InsertBloodPressureMeasurementCommand(long userID, BloodPressureMeasurement measurement, InsertCommandListener listener) {
    super(userID, measurement, listener);
  }


  @Override
  protected long insertToDatabase(long userID, BloodPressureMeasurement measurement2) throws DatabaseException {
    return dbBPM.addMeasurement(userID, measurement);
  }
}
