package cz.zcu.kiv.mobile.logger.data.database.commands;

import java.util.List;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.BloodPressureMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.blood_pressure.BloodPressureMeasurement;


public class InsertBloodPressureMeasurementBatchCommand extends AInsertMeasurementBatchCommand<BloodPressureMeasurement> {
  protected static final BloodPressureMeasurementTable dbBPM = Application.getInstance().getDatabase().getBloodPressureMeasurementTable();

  
  public InsertBloodPressureMeasurementBatchCommand(long userID, List<BloodPressureMeasurement> measurements, boolean ignoreDuplicates, InsertBatchCommandListener listener) {
    super(userID, measurements, ignoreDuplicates, listener);
  }


  @Override
  protected List<Long> insertToDatabase(long userID, List<BloodPressureMeasurement> measurements) throws DatabaseException {
    return dbBPM.addMeasurements(userID, measurements, ignoreDuplicates);
  }
}
