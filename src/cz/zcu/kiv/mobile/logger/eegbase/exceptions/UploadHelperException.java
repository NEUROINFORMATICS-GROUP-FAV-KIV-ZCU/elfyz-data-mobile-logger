package cz.zcu.kiv.mobile.logger.eegbase.exceptions;


public class UploadHelperException extends Exception {
  private static final long serialVersionUID = 1L;

  
  public UploadHelperException() { }

  public UploadHelperException(String detailMessage) {
    super(detailMessage);
  }

  public UploadHelperException(Throwable throwable) {
    super(throwable);
  }

  public UploadHelperException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }
}
