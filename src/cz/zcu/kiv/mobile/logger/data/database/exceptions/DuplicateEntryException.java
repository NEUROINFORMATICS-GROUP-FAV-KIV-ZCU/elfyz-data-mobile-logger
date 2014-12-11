package cz.zcu.kiv.mobile.logger.data.database.exceptions;


public class DuplicateEntryException extends DatabaseException {
  private static final long serialVersionUID = 1L;

  
  public DuplicateEntryException() {
    super();
  }

  public DuplicateEntryException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public DuplicateEntryException(String detailMessage) {
    super(detailMessage);
  }

  public DuplicateEntryException(Throwable throwable) {
    super(throwable);
  }
}
