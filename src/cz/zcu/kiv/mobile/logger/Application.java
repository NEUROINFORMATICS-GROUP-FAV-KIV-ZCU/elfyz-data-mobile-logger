package cz.zcu.kiv.mobile.logger;

import android.content.Context;
import cz.zcu.kiv.mobile.logger.data.database.Database;


public class Application extends android.app.Application {
  private Database db; 
  private static Context context;
  
  public Application() {
    super();
    
    db = new Database(this);
    context = this;
  }

  
  public Database getDatabase() {
    return db;
  }


  public static String getStringResource(int textId) {
    return context.getString(textId); //TODO
  }
}
