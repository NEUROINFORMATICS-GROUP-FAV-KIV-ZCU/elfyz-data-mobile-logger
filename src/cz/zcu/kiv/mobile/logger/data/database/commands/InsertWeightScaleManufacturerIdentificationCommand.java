package cz.zcu.kiv.mobile.logger.data.database.commands;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleManufacturerIdentificationTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.WeightScaleManufacturerIdentification;


public class InsertWeightScaleManufacturerIdentificationCommand extends AInsertMeasurementCommand<WeightScaleManufacturerIdentification> {
  protected static final WeightScaleManufacturerIdentificationTable dbWsMi = Application.getInstance().getDatabase().getWeightScaleManufacturerIdentificationTable();


  public InsertWeightScaleManufacturerIdentificationCommand(long userID, WeightScaleManufacturerIdentification data, InsertCommandListener listener) {
    super(userID, data, listener);
  }


  @Override
  protected long insertToDatabase(long userID, WeightScaleManufacturerIdentification measurement) throws DatabaseException {
    return dbWsMi.addManufacturerIdentification(userID, measurement);
  }
}
