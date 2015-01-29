package cz.zcu.kiv.mobile.logger.data.database.commands;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateVersionAndModelTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateVersionAndModel;


public class InsertHeartRateVersionAndModelCommand extends AInsertMeasurementCommand<HeartRateVersionAndModel> {
  protected static final HeartRateVersionAndModelTable dbHRVam = Application.getInstance().getDatabase().getHeartRateVersionAndModelTable();


  public InsertHeartRateVersionAndModelCommand(long userID, HeartRateVersionAndModel data, InsertCommandListener listener) {
    super(userID, data, listener);
  }


  @Override
  protected long insertToDatabase(long userID, HeartRateVersionAndModel measurement) throws DatabaseException {
    return dbHRVam.addVersionAndModel(userID, measurement);
  }
}
