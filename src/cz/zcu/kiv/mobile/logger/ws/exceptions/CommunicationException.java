package cz.zcu.kiv.mobile.logger.ws.exceptions;


public class CommunicationException extends Exception {
  private static final long serialVersionUID = 1L;
  

  public CommunicationException() {
    super();
  }

  public CommunicationException(String detailMessage) {
    super(detailMessage);
  }

  public CommunicationException(Throwable throwable) {
    super(throwable);
  }

  public CommunicationException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }
}
