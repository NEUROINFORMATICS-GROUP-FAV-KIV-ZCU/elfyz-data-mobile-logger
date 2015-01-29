package cz.zcu.kiv.mobile.logger.data.database.commands;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.WeightScaleBasicMeasurement;


public class InsertWeightScaleBasicMeasurementCommand extends AInsertMeasurementCommand<WeightScaleBasicMeasurement> {
  protected static final WeightScaleMeasurementTable dbWsBm = Application.getInstance().getDatabase().getWeightScaleMeasurementTable();


  public InsertWeightScaleBasicMeasurementCommand(long userID, WeightScaleBasicMeasurement data, InsertCommandListener listener) {
    super(userID, data, listener);
  }


  @Override
  protected long insertToDatabase(long userID, WeightScaleBasicMeasurement measurement) throws DatabaseException {
    return dbWsBm.addBasicMeasurement(userID, measurement);
  }
}
