package cz.zcu.kiv.mobile.logger.data.database.commands;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateManufacturerAndSerialTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.devices.heart_rate.HeartRateManufacturerAndSerial;


public class InsertHeartRateManufacturerAndSerialCommand extends AInsertMeasurementCommand<HeartRateManufacturerAndSerial> {
  protected static final HeartRateManufacturerAndSerialTable dbHRMas = Application.getInstance().getDatabase().getHeartRateManufacturerAndSerialTable();


  public InsertHeartRateManufacturerAndSerialCommand(long userID, HeartRateManufacturerAndSerial data, InsertCommandListener listener) {
    super(userID, data, listener);
  }


  @Override
  protected long insertToDatabase(long userID, HeartRateManufacturerAndSerial measurement) throws DatabaseException {
    return dbHRMas.addManufacturerAndSerial(userID, measurement);
  }
}
