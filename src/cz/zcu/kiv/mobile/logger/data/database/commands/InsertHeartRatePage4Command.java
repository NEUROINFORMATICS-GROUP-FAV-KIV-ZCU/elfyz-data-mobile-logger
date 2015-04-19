package cz.zcu.kiv.mobile.logger.data.database.commands;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.HeartRatePage4Table;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.heart_rate.HeartRatePage4;


public class InsertHeartRatePage4Command extends AInsertMeasurementCommand<HeartRatePage4> {
  protected static final HeartRatePage4Table dbHRP4 = Application.getInstance().getDatabase().getHeartRatePage4Table();


  public InsertHeartRatePage4Command(long userID, HeartRatePage4 data, InsertCommandListener listener) {
    super(userID, data, listener);
  }


  @Override
  protected long insertToDatabase(long userID, HeartRatePage4 measurement) throws DatabaseException {
    return dbHRP4.addAdditionalData(userID, measurement);
  }
}
