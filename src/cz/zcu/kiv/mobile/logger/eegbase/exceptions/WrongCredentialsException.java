package cz.zcu.kiv.mobile.logger.eegbase.exceptions;


public class WrongCredentialsException extends Exception {
  private static final long serialVersionUID = 1L;

  
  public WrongCredentialsException() {
    super();
  }

  public WrongCredentialsException(String detailMessage) {
    super(detailMessage);
  }

  public WrongCredentialsException(Throwable throwable) {
    super(throwable);
  }

  public WrongCredentialsException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }
}
