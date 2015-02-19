package cz.zcu.kiv.mobile.logger.data.database.exceptions;


public class DuplicateEntryException extends DatabaseException {
  private static final long serialVersionUID = 1L;
  private String columnName;

  
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

  public DuplicateEntryException(Throwable throwable, String columnName) {  //TODO místo message jen column name a sestavit zprávu
    super(throwable);
    this.columnName = columnName;
  }
  
  
  public String getColumnName() {
    return columnName;
  }
  
  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }
}
