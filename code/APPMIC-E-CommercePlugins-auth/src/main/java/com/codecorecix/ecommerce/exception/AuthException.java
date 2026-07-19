package com.codesoft.exception;

public class AuthException extends BaseException {

  public AuthException(final AuthMessageEnum message) {
    super(message);
  }
}
