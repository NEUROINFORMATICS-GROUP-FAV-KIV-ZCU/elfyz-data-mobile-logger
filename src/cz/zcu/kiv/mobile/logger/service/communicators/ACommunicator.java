package cz.zcu.kiv.mobile.logger.service.communicators;

import java.io.Closeable;

import android.content.Context;


public abstract class ACommunicator implements Closeable {
  protected Context context;
  
  
  public ACommunicator(Context context) {
    this.context = context;
  }
  
  
  @Override
  public void close() {
    context = null;
  }
}
