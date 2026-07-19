package com.codesoft.utils;

import org.springframework.http.HttpStatus;

public interface IErrorCode {

  String getErrorMessage();

  Integer getErrorCode();

  HttpStatus getHttpStatus();
}
