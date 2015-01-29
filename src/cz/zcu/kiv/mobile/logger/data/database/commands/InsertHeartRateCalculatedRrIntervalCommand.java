package cz.zcu.kiv.mobile.logger.data.database.commands;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateCalculatedRrIntervalTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateCalculatedRrInterval;


public class InsertHeartRateCalculatedRrIntervalCommand extends AInsertMeasurementCommand<HeartRateCalculatedRrInterval> {
  protected static final HeartRateCalculatedRrIntervalTable dbHRCrr = Application.getInstance().getDatabase().getHeartRateCalculatedRrIntervalTable();


  public InsertHeartRateCalculatedRrIntervalCommand(long userID, HeartRateCalculatedRrInterval data, InsertCommandListener listener) {
    super(userID, data, listener);
  }


  @Override
  protected long insertToDatabase(long userID, HeartRateCalculatedRrInterval measurement) throws DatabaseException {
    return dbHRCrr.addCalculatedRrIntervalData(userID, measurement);
  }
}
