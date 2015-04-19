package cz.zcu.kiv.mobile.logger.sync;

import java.util.List;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;
import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.data.database.BloodPressureMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.Database;
import cz.zcu.kiv.mobile.logger.data.database.GlucoseMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateCalculatedRrIntervalTable;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateCumulativeOperatingTimeTable;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateManufacturerAndSerialTable;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.HeartRatePage4Table;
import cz.zcu.kiv.mobile.logger.data.database.HeartRateVersionAndModelTable;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleBatteryStatusTable;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleManufacturerIdentificationTable;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleManufacturerSpecificDataTable;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleMeasurementTable;
import cz.zcu.kiv.mobile.logger.data.database.WeightScaleProductInformationTable;
import cz.zcu.kiv.mobile.logger.data.database.exceptions.DatabaseException;
import cz.zcu.kiv.mobile.logger.data.types.AutoSync;
import cz.zcu.kiv.mobile.logger.data.types.Profile;
import cz.zcu.kiv.mobile.logger.eegbase.EegbaseRest;
import cz.zcu.kiv.mobile.logger.eegbase.data.add_experiment_parameters.ExperimentParametersData;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.CommunicationException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.UploadHelperException;
import cz.zcu.kiv.mobile.logger.eegbase.exceptions.WrongCredentialsException;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.BloodPressureMeasurementDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.GlucoseMeasurementDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.HRCalculatedRrIntervalDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.HRCumulativeOperatingTimeDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.HRManufacturerAndSerialDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.HRMeasurementDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.HRPage4DbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.HRVersionAndModelDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.WSBatteryStatusDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.WSManufacturerIdentificationDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.WSManufacturerSpecificDataDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.WSMeasurementDbUploadHelper;
import cz.zcu.kiv.mobile.logger.eegbase.upload_helpers.WSProductInformationDbUploadHelper;


/**
 * Handle the transfer of data between a server and an app,
 * using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
  private static final String TAG = AbstractThreadedSyncAdapter.class.getSimpleName();
  
  private static final int CHUNK_SIZE = 100;  //TODO from preferences
  

  /**
   * Set up the sync adapter
   */
  public SyncAdapter(Context context, boolean autoInitialize) {
    super(context, autoInitialize);
  }

  /**
   * Set up the sync adapter. This form of the constructor maintains
   * compatibility with Android 3.0 and later platform versions
   */
  public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
    super(context, autoInitialize, allowParallelSyncs);
  }

  
  @Override
  public void onPerformSync(Account account, Bundle extras, String authority,
          ContentProviderClient provider, SyncResult syncResult) {
    long userID = Long.parseLong(account.name);
    
    Database db = Application.getInstance().getDatabase();
    
    Profile userProfile = null;
    try {
      db.getProfileTable().getProfile(userID);
    }
    catch (DatabaseException e) {
      Log.e(TAG, "Failed to load user profile.");
      return;
    }
    
    List<AutoSync> autoSyncs;
    try {
      autoSyncs = db.getAutoSyncTable().getRecords(userID);
    }
    catch (DatabaseException e) {
      Log.e(TAG, "Failed to get auto sync records.", e);
      return;
    }
      
    for (AutoSync autoSync : autoSyncs) {
      try {
        switch (autoSync.getTableID()) {
          case Database.TABLE_ID_BP:
            syncBp(userProfile, autoSync.getExperimentID(), db.getBloodPressureMeasurementTable());
            break;
            
          case Database.TABLE_ID_GM:
            syncGm(userProfile, autoSync.getExperimentID(), db.getGlucoseMeasurementTable());
            break;
            
          case Database.TABLE_ID_HR:
            syncHr(userProfile, autoSync.getExperimentID(), db.getHeartRateMeasurementTable());
            break;
            
          case Database.TABLE_ID_HR_CRRI:
            syncHrCrri(userProfile, autoSync.getExperimentID(), db.getHeartRateCalculatedRrIntervalTable());
            break;
            
          case Database.TABLE_ID_HR_COP:
            syncHrCop(userProfile, autoSync.getExperimentID(), db.getHeartRateCumulativeOperatingTimeTable());
            break;
            
          case Database.TABLE_ID_HR_MAN:
            syncHrMan(userProfile, autoSync.getExperimentID(), db.getHeartRateManufacturerAndSerialTable());
            break;
            
          case Database.TABLE_ID_HR_P4:
            syncHrP4(userProfile, autoSync.getExperimentID(), db.getHeartRatePage4Table());
            break;
            
          case Database.TABLE_ID_HR_VAM:
            syncHrVam(userProfile, autoSync.getExperimentID(), db.getHeartRateVersionAndModelTable());
            break;
            
          case Database.TABLE_ID_WS:
            syncWs(userProfile, autoSync.getExperimentID(), db.getWeightScaleMeasurementTable());
            break;
            
          case Database.TABLE_ID_WS_BAT:
            syncWsBat(userProfile, autoSync.getExperimentID(), db.getWeightScaleBatteryStatusTable());
            break;
            
          case Database.TABLE_ID_WS_MID:
            syncWsMid(userProfile, autoSync.getExperimentID(), db.getWeightScaleManufacturerIdentificationTable());
            break;
            
          case Database.TABLE_ID_WS_MS:
            syncWsMs(userProfile, autoSync.getExperimentID(), db.getWeightScaleManufacturerSpecificDataTable());
            break;
            
          case Database.TABLE_ID_WS_PI:
            syncWsPi(userProfile, autoSync.getExperimentID(), db.getWeightScaleProductInformationTable());
            break;
            
          default:
            Log.w(TAG, "Unknown table ID: " + autoSync.getTableID());
            break;
        }
      }
      catch (DatabaseException e) {
        Log.e(TAG, "Failed to auto sync.", e);
      }
      catch (UploadHelperException e) {
        Log.e(TAG, "Failed to auto sync.", e);
      }
      catch (CommunicationException e) {
        Log.e(TAG, "Failed to auto sync.", e);
      }
      catch (WrongCredentialsException e) {
        Log.e(TAG, "Failed to auto sync.", e);
      }
    }
  }

  private void syncWsPi(Profile userProfile, String experimentID, WeightScaleProductInformationTable db) throws DatabaseException, UploadHelperException, CommunicationException, WrongCredentialsException {
    long[] ids = db.getNotUploadedIDs(userProfile.getId(), CHUNK_SIZE);

    String parameterName = Application.getPreferences().getString("pref_gen_par_name_ws_pi", "WS Product Info");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    
    ExperimentParametersData parameters = new WSProductInformationDbUploadHelper(parameterName, 0.0, ids, append).buildParameters();
    EegbaseRest.uploadGenericParameters(userProfile.getEmail(), userProfile.getEegbasePassword(), experimentID, parameters);
    db.setUploaded(ids);
  }

  private void syncWsMs(Profile userProfile, String experimentID, WeightScaleManufacturerSpecificDataTable db) throws DatabaseException, UploadHelperException, CommunicationException, WrongCredentialsException {
    long[] ids = db.getNotUploadedIDs(userProfile.getId(), CHUNK_SIZE);

    String parameterName = Application.getPreferences().getString("pref_gen_par_name_ws_ms", "WS Manufacturer Specific");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    
    ExperimentParametersData parameters = new WSManufacturerSpecificDataDbUploadHelper(parameterName, 0.0, ids, append).buildParameters();
    EegbaseRest.uploadGenericParameters(userProfile.getEmail(), userProfile.getEegbasePassword(), experimentID, parameters);
    db.setUploaded(ids);
  }

  private void syncWsMid(Profile userProfile, String experimentID, WeightScaleManufacturerIdentificationTable db) throws DatabaseException, UploadHelperException, CommunicationException, WrongCredentialsException {
    long[] ids = db.getNotUploadedIDs(userProfile.getId(), CHUNK_SIZE);

    String parameterName = Application.getPreferences().getString("pref_gen_par_name_ws_mid", "WS Manufacturer Identification");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    
    ExperimentParametersData parameters = new WSManufacturerIdentificationDbUploadHelper(parameterName, 0.0, ids, append).buildParameters();
    EegbaseRest.uploadGenericParameters(userProfile.getEmail(), userProfile.getEegbasePassword(), experimentID, parameters);
    db.setUploaded(ids);
  }

  private void syncWsBat(Profile userProfile, String experimentID, WeightScaleBatteryStatusTable db) throws DatabaseException, UploadHelperException, CommunicationException, WrongCredentialsException {
    long[] ids = db.getNotUploadedIDs(userProfile.getId(), CHUNK_SIZE);

    String parameterName = Application.getPreferences().getString("pref_gen_par_name_ws_bs", "WS Battery Status");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    
    ExperimentParametersData parameters = new WSBatteryStatusDbUploadHelper(parameterName, 0.0, ids, append).buildParameters();
    EegbaseRest.uploadGenericParameters(userProfile.getEmail(), userProfile.getEegbasePassword(), experimentID, parameters);
    db.setUploaded(ids);
  }

  private void syncWs(Profile userProfile, String experimentID, WeightScaleMeasurementTable db) throws DatabaseException, UploadHelperException, CommunicationException, WrongCredentialsException {
    long[] ids = db.getNotUploadedIDs(userProfile.getId(), CHUNK_SIZE);

    String parameterName = Application.getPreferences().getString("pref_gen_par_name_ws_measurement", "WS Measurement");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    
    ExperimentParametersData parameters = new WSMeasurementDbUploadHelper(parameterName, 0.0, ids, append).buildParameters();
    EegbaseRest.uploadGenericParameters(userProfile.getEmail(), userProfile.getEegbasePassword(), experimentID, parameters);
    db.setUploaded(ids);
  }

  private void syncHrVam(Profile userProfile, String experimentID, HeartRateVersionAndModelTable db) throws DatabaseException, UploadHelperException, CommunicationException, WrongCredentialsException {
    long[] ids = db.getNotUploadedIDs(userProfile.getId(), CHUNK_SIZE);

    String parameterName = Application.getPreferences().getString("pref_gen_par_name_hr_vam", "HR Version And Model");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    
    ExperimentParametersData parameters = new HRVersionAndModelDbUploadHelper(parameterName, 0.0, ids, append).buildParameters();
    EegbaseRest.uploadGenericParameters(userProfile.getEmail(), userProfile.getEegbasePassword(), experimentID, parameters);
    db.setUploaded(ids);
  }

  private void syncHrP4(Profile userProfile, String experimentID, HeartRatePage4Table db) throws DatabaseException, UploadHelperException, CommunicationException, WrongCredentialsException {
    long[] ids = db.getNotUploadedIDs(userProfile.getId(), CHUNK_SIZE);

    String parameterName = Application.getPreferences().getString("pref_gen_par_name_hr_p4", "HR Page 4");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    
    ExperimentParametersData parameters = new HRPage4DbUploadHelper(parameterName, 0.0, ids, append).buildParameters();
    EegbaseRest.uploadGenericParameters(userProfile.getEmail(), userProfile.getEegbasePassword(), experimentID, parameters);
    db.setUploaded(ids);
  }

  private void syncHrMan(Profile userProfile, String experimentID, HeartRateManufacturerAndSerialTable db) throws DatabaseException, UploadHelperException, CommunicationException, WrongCredentialsException {
    long[] ids = db.getNotUploadedIDs(userProfile.getId(), CHUNK_SIZE);

    String parameterName = Application.getPreferences().getString("pref_gen_par_name_hr_mas", "HR Manufacturer And Serial");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    
    ExperimentParametersData parameters = new HRManufacturerAndSerialDbUploadHelper(parameterName, 0.0, ids, append).buildParameters();
    EegbaseRest.uploadGenericParameters(userProfile.getEmail(), userProfile.getEegbasePassword(), experimentID, parameters);
    db.setUploaded(ids);
  }

  private void syncHrCop(Profile userProfile, String experimentID, HeartRateCumulativeOperatingTimeTable db) throws DatabaseException, UploadHelperException, CommunicationException, WrongCredentialsException {
    long[] ids = db.getNotUploadedIDs(userProfile.getId(), CHUNK_SIZE);

    String parameterName = Application.getPreferences().getString("pref_gen_par_name_hr_copt", "HR Cumulative Operating Time");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    
    ExperimentParametersData parameters = new HRCumulativeOperatingTimeDbUploadHelper(parameterName, 0.0, ids, append).buildParameters();
    EegbaseRest.uploadGenericParameters(userProfile.getEmail(), userProfile.getEegbasePassword(), experimentID, parameters);
    db.setUploaded(ids);
  }

  private void syncHrCrri(Profile userProfile, String experimentID, HeartRateCalculatedRrIntervalTable db) throws DatabaseException, UploadHelperException, CommunicationException, WrongCredentialsException {
    long[] ids = db.getNotUploadedIDs(userProfile.getId(), CHUNK_SIZE);

    String parameterName = Application.getPreferences().getString("pref_gen_par_name_hr_crri", "HR Calculated RR Interval");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    
    ExperimentParametersData parameters = new HRCalculatedRrIntervalDbUploadHelper(parameterName, 0.0, ids, append).buildParameters();
    EegbaseRest.uploadGenericParameters(userProfile.getEmail(), userProfile.getEegbasePassword(), experimentID, parameters);
    db.setUploaded(ids);
  }

  private void syncHr(Profile userProfile, String experimentID, HeartRateMeasurementTable db) throws DatabaseException, UploadHelperException, CommunicationException, WrongCredentialsException {
    long[] ids = db.getNotUploadedIDs(userProfile.getId(), CHUNK_SIZE);

    String parameterName = Application.getPreferences().getString("pref_gen_par_name_hr_measurement", "HR Measurement");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    
    ExperimentParametersData parameters = new HRMeasurementDbUploadHelper(parameterName, 0.0, ids, append).buildParameters();
    EegbaseRest.uploadGenericParameters(userProfile.getEmail(), userProfile.getEegbasePassword(), experimentID, parameters);
    db.setUploaded(ids);
  }

  private void syncGm(Profile userProfile, String experimentID, GlucoseMeasurementTable db) throws DatabaseException, UploadHelperException, CommunicationException, WrongCredentialsException {
    long[] ids = db.getNotUploadedIDs(userProfile.getId(), CHUNK_SIZE);
    
    String parameterName = Application.getPreferences().getString("pref_gen_par_name_gm", "Glucose measurements");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    
    ExperimentParametersData parameters = new GlucoseMeasurementDbUploadHelper(parameterName, 0.0, ids, append).buildParameters();
    EegbaseRest.uploadGenericParameters(userProfile.getEmail(), userProfile.getEegbasePassword(), experimentID, parameters);
    db.setUploaded(ids);
  }

  private void syncBp(Profile userProfile, String experimentID, BloodPressureMeasurementTable db) throws DatabaseException, UploadHelperException, CommunicationException, WrongCredentialsException {
    long[] ids = db.getNotUploadedIDs(userProfile.getId(), CHUNK_SIZE);
    
    String parameterName = Application.getPreferences().getString("pref_gen_par_name_bp", "Blood pressure measurements");
    boolean append = Application.getPreferences().getBoolean("pref_gen_par_append", true);
    
    ExperimentParametersData parameters = new BloodPressureMeasurementDbUploadHelper(parameterName, 0.0, ids, append).buildParameters();
    EegbaseRest.uploadGenericParameters(userProfile.getEmail(), userProfile.getEegbasePassword(), experimentID, parameters);
    db.setUploaded(ids);
  }
}
