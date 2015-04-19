package cz.zcu.kiv.mobile.logger.data.database.commands;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleManufacturerSpecificDataTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.weight_scale.WeightScaleManufacturerSpecificData;


public class InsertWeightScaleManufacturerSpecificDataCommand extends AInsertMeasurementCommand<WeightScaleManufacturerSpecificData> {
  protected static final WeightScaleManufacturerSpecificDataTable dbWsMs = Application.getInstance().getDatabase().getWeightScaleManufacturerSpecificDataTable();


  public InsertWeightScaleManufacturerSpecificDataCommand(long userID, WeightScaleManufacturerSpecificData data, InsertCommandListener listener) {
    super(userID, data, listener);
  }


  @Override
  protected long insertToDatabase(long userID, WeightScaleManufacturerSpecificData measurement) throws DatabaseException {
    return dbWsMs.addManufacturerSpecificData(userID, measurement);
  }
}
