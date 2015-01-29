package cz.zcu.kiv.mobile.logger.data;


public class AsyncTaskResult<R> {
  private R result;
  private Exception error;
  
  
  public AsyncTaskResult() { }
  
  public AsyncTaskResult(R result) {
    this.result = result;
  }
  
  public AsyncTaskResult(Exception error) {
    this.error = error;
  }
  
  public AsyncTaskResult(R result, Exception error) {
    this.result = result;
    this.error = error;
  }
  
  
  public R getResult() {
    return result;
  }
  
  public void setResult(R result) {
    this.result = result;
  }
  
  public Exception getError() {
    return error;
  }
  
  public void setError(Exception error) {
    this.error = error;
  }
}
