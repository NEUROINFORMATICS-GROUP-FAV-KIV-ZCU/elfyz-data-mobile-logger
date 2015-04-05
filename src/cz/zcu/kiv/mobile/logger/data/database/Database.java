package cz.zcu.kiv.mobile.logger.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Database {
  private static final String DATABASE_NAME = "elfyz-data-mobile-logger.db";
  private static final int DATABASE_VERSION = 11;

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


  public Database(Context context){
    openHelper = new DatabaseHelper(context);
    
    profileTable = new ProfileTable(openHelper);
    bpmTable = new BloodPressureMeasurementTable(openHelper);
    wsTable = new WeightScaleMeasurementTable(openHelper);
    hrTable = new HeartRateMeasurementTable(openHelper);
    hrP4Table = new HeartRatePage4Table(openHelper);
    hrCotTable = new HeartRateCumulativeOperatingTimeTable(openHelper);
    hrMasTable = new HeartRateManufacturerAndSerialTable(openHelper);
    hrVamTable = new HeartRateVersionAndModelTable(openHelper);
    hrCrrTable = new HeartRateCalculatedRrIntervalTable(openHelper);
    wsBsTable = new WeightScaleBatteryStatusTable(openHelper);
    wsMiTable = new WeightScaleManufacturerIdentificationTable(openHelper);
    wsMsTable = new WeightScaleManufacturerSpecificDataTable(openHelper);
    wsPiTable = new WeightScaleProductInformationTable(openHelper);
    gTable = new GlucoseMeasurementTable(openHelper);
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
      profileTable.onCreate(db);
      bpmTable.onCreate(db);
      hrTable.onCreate(db);
      hrP4Table.onCreate(db);
      hrCotTable.onCreate(db);
      hrMasTable.onCreate(db);
      hrVamTable.onCreate(db);
      hrCrrTable.onCreate(db);
      wsTable.onCreate(db);
      wsBsTable.onCreate(db);
      wsMiTable.onCreate(db);
      wsMsTable.onCreate(db);
      wsPiTable.onCreate(db);
      gTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
      profileTable.onUpgrade(db, oldVersion, currentVersion);
      bpmTable.onUpgrade(db, oldVersion, currentVersion);
      wsTable.onUpgrade(db, oldVersion, currentVersion);
      hrTable.onUpgrade(db, oldVersion, currentVersion);
      hrP4Table.onUpgrade(db, oldVersion, currentVersion);
      hrCotTable.onUpgrade(db, oldVersion, currentVersion);
      hrMasTable.onUpgrade(db, oldVersion, currentVersion);
      hrVamTable.onUpgrade(db, oldVersion, currentVersion);
      hrCrrTable.onUpgrade(db, oldVersion, currentVersion);
      wsBsTable.onUpgrade(db, oldVersion, currentVersion);
      wsMiTable.onUpgrade(db, oldVersion, currentVersion);
      wsMsTable.onUpgrade(db, oldVersion, currentVersion);
      wsPiTable.onUpgrade(db, oldVersion, currentVersion);
      gTable.onUpgrade(db, oldVersion, currentVersion);
    }
  }
}
