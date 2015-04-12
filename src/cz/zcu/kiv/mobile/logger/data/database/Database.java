package cz.zcu.kiv.mobile.logger.data.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Database {
  private static final String DATABASE_NAME = "elfyz-data-mobile-logger.db";
  private static final int DATABASE_VERSION = 13;

  private SQLiteOpenHelper openHelper;
  
  private ProfileTable profileTable;
  private BloodPressureMeasurementTable bpmTable;
  private HeartRateMeasurementTable hrTable;
  private HeartRatePage4Table hrP4Table;
  private HeartRateCumulativeOperatingTimeTable hrCotTable;
  private HeartRateManufacturerAndSerialTable hrMasTable;
  private HeartRateVersionAndModelTable hrVamTable;
  private HeartRateCalculatedRrIntervalTable hrCrrTable;
  private WeightScaleMeasurementTable wsTable;
  private WeightScaleBatteryStatusTable wsBsTable;
  private WeightScaleManufacturerIdentificationTable wsMiTable;
  private WeightScaleManufacturerSpecificDataTable wsMsTable;
  private WeightScaleProductInformationTable wsPiTable;
  private GlucoseMeasurementTable gTable;
  
  private List<ATable<?>> tables;
  private List<ARecordTable<?>> recordTables;


  public Database(Context context){
    openHelper = new DatabaseHelper(context);
    
    recordTables = new ArrayList<ARecordTable<?>>(16);
    recordTables.add(bpmTable = new BloodPressureMeasurementTable(openHelper));
    recordTables.add(wsTable = new WeightScaleMeasurementTable(openHelper));
    recordTables.add(hrTable = new HeartRateMeasurementTable(openHelper));
    recordTables.add(hrP4Table = new HeartRatePage4Table(openHelper));
    recordTables.add(hrCotTable = new HeartRateCumulativeOperatingTimeTable(openHelper));
    recordTables.add(hrMasTable = new HeartRateManufacturerAndSerialTable(openHelper));
    recordTables.add(hrVamTable = new HeartRateVersionAndModelTable(openHelper));
    recordTables.add(hrCrrTable = new HeartRateCalculatedRrIntervalTable(openHelper));
    recordTables.add(wsBsTable = new WeightScaleBatteryStatusTable(openHelper));
    recordTables.add(wsMiTable = new WeightScaleManufacturerIdentificationTable(openHelper));
    recordTables.add(wsMsTable = new WeightScaleManufacturerSpecificDataTable(openHelper));
    recordTables.add(wsPiTable = new WeightScaleProductInformationTable(openHelper));
    recordTables.add(gTable = new GlucoseMeasurementTable(openHelper));

    tables = new ArrayList<ATable<?>>(16);
    tables.add(profileTable = new ProfileTable(openHelper));
    tables.addAll(recordTables);
  }
  
  
  public ProfileTable getProfileTable() {
    return profileTable;
  }

  public BloodPressureMeasurementTable getBloodPressureMeasurementTable() {
    return bpmTable;
  }
  
  public HeartRateMeasurementTable getHeartRateMeasurementTable() {
    return hrTable;
  }
  
  public HeartRatePage4Table getHeartRatePage4Table() {
    return hrP4Table;
  }
  
  public HeartRateCumulativeOperatingTimeTable getHeartRateCumulativeOperatingTimeTable() {
    return hrCotTable;
  }
  
  public HeartRateManufacturerAndSerialTable getHeartRateManufacturerAndSerialTable() {
    return hrMasTable;
  }
  
  public HeartRateVersionAndModelTable getHeartRateVersionAndModelTable() {
    return hrVamTable;
  }
  
  public HeartRateCalculatedRrIntervalTable getHeartRateCalculatedRrIntervalTable() {
    return hrCrrTable;
  }
  
  public WeightScaleMeasurementTable getWeightScaleMeasurementTable() {
    return wsTable;
  }
  
  public WeightScaleBatteryStatusTable getWeightScaleBatteryStatusTable() {
    return wsBsTable;
  }
  
  public WeightScaleManufacturerIdentificationTable getWeightScaleManufacturerIdentificationTable() {
    return wsMiTable;
  }
  
  public WeightScaleManufacturerSpecificDataTable getWeightScaleManufacturerSpecificDataTable() {
    return wsMsTable;
  }
  
  public WeightScaleProductInformationTable getWeightScaleProductInformationTable() {
    return wsPiTable;
  }
  
  public GlucoseMeasurementTable getGlucoseMeasurementTable() {
    return gTable;
  }
  

  public void close(){
    openHelper.close();
  }

  

  private class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      for (ATable<?> table : tables) {
        table.onCreate(db);
      }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
      for (ATable<?> table : tables) {
        table.onUpgrade(db, oldVersion, currentVersion);
      }
    }
    
    @Override
    public void onOpen(SQLiteDatabase db) {
      super.onOpen(db);
      db.execSQL("PRAGMA foreign_keys=ON");
    }
  }
}
