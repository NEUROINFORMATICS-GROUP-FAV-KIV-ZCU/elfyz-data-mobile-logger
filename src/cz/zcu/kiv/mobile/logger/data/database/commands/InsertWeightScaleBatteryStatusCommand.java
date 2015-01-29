package cz.zcu.kiv.mobile.logger.data.database.commands;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleBatteryStatusTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.weight_scale.WeightScaleBatteryStatus;


public class InsertWeightScaleBatteryStatusCommand extends AInsertMeasurementCommand<WeightScaleBatteryStatus> {
  protected static final WeightScaleBatteryStatusTable dbWsBs = Application.getInstance().getDatabase().getWeightScaleBatteryStatusTable();


  public InsertWeightScaleBatteryStatusCommand(long userID, WeightScaleBatteryStatus data, InsertCommandListener listener) {
    super(userID, data, listener);
  }


  @Override
  protected long insertToDatabase(long userID, WeightScaleBatteryStatus measurement) throws DatabaseException {
    return dbWsBs.addBatteryStatus(userID, measurement);
  }
}
