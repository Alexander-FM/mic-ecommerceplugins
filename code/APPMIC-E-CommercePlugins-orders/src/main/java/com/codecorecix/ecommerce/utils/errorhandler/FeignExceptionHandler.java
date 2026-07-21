package com.codecorecix.ecommerce.utils.errorhandler;

import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FeignExceptionHandler {

    @ExceptionHandler(FeignException.Unauthorized.class)
    public ResponseEntity<GenericResponse<Object>> handleFeignUnauthorized(final FeignException.Unauthorized ex) {
        return new ResponseEntity<>(new GenericResponse<>(GenericResponseConstants.RPTA_ERROR, "The request to the product service is not authorized, verify the token", null), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(FeignException.Forbidden.class)
    public ResponseEntity<GenericResponse<Object>> handleFeignForbidden(final FeignException.Forbidden ex) {
        return new ResponseEntity<>(new GenericResponse<>(GenericResponseConstants.RPTA_ERROR, "Access to the product service is forbidden, verify the token", null), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<GenericResponse<Object>> handleFeignNotFound(final FeignException.NotFound ex) {
        return new ResponseEntity<>(new GenericResponse<>(GenericResponseConstants.RPTA_ERROR, "Requested products were not found", null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FeignException.InternalServerError.class)
    public ResponseEntity<GenericResponse<Object>> handleFeignInternalServerError(final FeignException.InternalServerError ex) {
        return new ResponseEntity<>(new GenericResponse<>(GenericResponseConstants.RPTA_ERROR, "Invalid product service endpoint configuration", null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<GenericResponse<Object>> handleFeignException(final FeignException ex) {
        return new ResponseEntity<>(new GenericResponse<>(GenericResponseConstants.RPTA_ERROR, "The product stock could not be verified, the service is not available", null), HttpStatus.SERVICE_UNAVAILABLE);
    }
}