package cz.zcu.kiv.mobile.logger.data.database.exceptions;


public class DuplicateEntryException extends DatabaseException {
  private static final long serialVersionUID = 1L;
  private String columnName;

  
  public DuplicateEntryException(String columnName, Throwable throwable) {
    super(prepareMessage(columnName), throwable);
    this.columnName = columnName;
  }

  public DuplicateEntryException(String columnName) {
    super(prepareMessage(columnName));
    this.columnName = columnName;
  }

  
  public String getColumnName() {
    return columnName;
  }

  
  private static String prepareMessage(String columnName) {
    return "Failed to persist data because of duplicit value on unique column: " + columnName;
  }
}
