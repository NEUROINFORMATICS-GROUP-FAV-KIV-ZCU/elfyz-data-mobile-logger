package cz.zcu.kiv.mobile.logger.data.database.commands;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.WeightScaleAdvancedMeasurement;


public class InsertWeightScaleAdvancedMeasurementCommand extends AInsertMeasurementCommand<WeightScaleAdvancedMeasurement> {
  protected static final WeightScaleMeasurementTable dbWsAm = Application.getInstance().getDatabase().getWeightScaleMeasurementTable();


  public InsertWeightScaleAdvancedMeasurementCommand(long userID, WeightScaleAdvancedMeasurement data, InsertCommandListener listener) {
    super(userID, data, listener);
  }


  @Override
  protected long insertToDatabase(long userID, WeightScaleAdvancedMeasurement measurement) throws DatabaseException {
    return dbWsAm.addAdvancedMeasurement(userID, measurement);
  }
}
