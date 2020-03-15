package com.utopia.datacell;

public class DatacellException extends RuntimeException {

  DatacellException(String message) {
    super(message);
  }

  DatacellException(Throwable cause) {
    super(cause);
  }

  DatacellException(String message, Throwable cause) {
    super(message, cause);
  }
}
