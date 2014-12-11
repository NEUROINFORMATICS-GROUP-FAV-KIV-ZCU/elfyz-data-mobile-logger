package cz.zcu.kiv.mobile.logger.data.database.exceptions;


public class DatabaseException extends Exception {
  private static final long serialVersionUID = 1L;

  
  public DatabaseException() {
    super();
  }

  public DatabaseException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public DatabaseException(String detailMessage) {
    super(detailMessage);
  }

  public DatabaseException(Throwable throwable) {
    super(throwable);
  }
}
