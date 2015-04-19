package cz.zcu.kiv.mobile.logger.data.database.commands;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateCumulativeOperatingTimeTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.heart_rate.HeartRateCumulativeOperatingTime;


public class InsertHeartRateCumulativeOperatingTimeCommand extends AInsertMeasurementCommand<HeartRateCumulativeOperatingTime> {
  protected static final HeartRateCumulativeOperatingTimeTable dbHrCot = Application.getInstance().getDatabase().getHeartRateCumulativeOperatingTimeTable();


  public InsertHeartRateCumulativeOperatingTimeCommand(long userID, HeartRateCumulativeOperatingTime data, InsertCommandListener listener) {
    super(userID, data, listener);
  }


  @Override
  protected long insertToDatabase(long userID, HeartRateCumulativeOperatingTime measurement) throws DatabaseException {
    return dbHrCot.addCumulativeOperatingTime(userID, measurement);
  }
}
