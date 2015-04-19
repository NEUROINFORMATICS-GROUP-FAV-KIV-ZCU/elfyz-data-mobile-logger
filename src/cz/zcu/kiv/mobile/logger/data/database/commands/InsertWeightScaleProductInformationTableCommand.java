package cz.zcu.kiv.mobile.logger.data.database.commands;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleProductInformationTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.weight_scale.WeightScaleProductInformation;


public class InsertWeightScaleProductInformationTableCommand extends AInsertMeasurementCommand<WeightScaleProductInformation> {
  protected static final WeightScaleProductInformationTable dbWsPi = Application.getInstance().getDatabase().getWeightScaleProductInformationTable();


  public InsertWeightScaleProductInformationTableCommand(long userID, WeightScaleProductInformation data, InsertCommandListener listener) {
    super(userID, data, listener);
  }


  @Override
  protected long insertToDatabase(long userID, WeightScaleProductInformation measurement) throws DatabaseException {
    return dbWsPi.addProductInformation(userID, measurement);
  }
}
