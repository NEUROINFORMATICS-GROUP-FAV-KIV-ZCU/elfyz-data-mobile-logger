package cz.zcu.kiv.mobile.logger.data.database.commands;

import java.util.List;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.GlucoseMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.fora.glucose.GlucoseMeasurement;


public class InsertGlucoseMeasurementBatchCommand extends AInsertMeasurementBatchCommand<GlucoseMeasurement> {
  protected static final GlucoseMeasurementTable dbGM = Application.getInstance().getDatabase().getGlucoseMeasurementTable();

  
  public InsertGlucoseMeasurementBatchCommand(long userID, List<GlucoseMeasurement> measurements, boolean ignoreDuplicates, InsertBatchCommandListener listener) {
    super(userID, measurements, ignoreDuplicates, listener);
  }


  @Override
  protected List<Long> insertToDatabase(long userID, List<GlucoseMeasurement> measurements) throws DatabaseException {
    return dbGM.addMeasurements(userID, measurements, ignoreDuplicates);
  }
}
