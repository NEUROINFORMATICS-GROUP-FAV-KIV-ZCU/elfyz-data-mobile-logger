package cz.zcu.kiv.mobile.logger.data.database.exceptions;


public class EntryNotFoundException extends DatabaseException {
  private static final long serialVersionUID = 1L;

  
  public EntryNotFoundException() {
    super();
  }

  public EntryNotFoundException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public EntryNotFoundException(String detailMessage) {
    super(detailMessage);
  }

  public EntryNotFoundException(Throwable throwable) {
    super(throwable);
  }
}
