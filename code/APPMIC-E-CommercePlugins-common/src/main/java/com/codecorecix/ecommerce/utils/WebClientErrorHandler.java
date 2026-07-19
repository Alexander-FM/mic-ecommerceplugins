package com.codesoft.utils;

import java.util.Map;

import com.codesoft.exception.BaseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnClass(name = "org.springframework.web.reactive.function.client.WebClient")
public class WebClientErrorHandler {

  private final ObjectMapper objectMapper;

  /**
   * Procesa la excepción y devuelve la BaseException adecuada o la específica del servicio.
   *
   * @param ex La excepción original capturada. //* @param objectMapper Necesario para deserializar el error upstream.
   * @param serviceUnavailableCode El enum de error a usar si el servicio está caído (ej. AUTH_EMPLOYEE_SERVICE_UNAVAILABLE).
   * @return RuntimeException lista para ser lanzada.
   */
  public RuntimeException handle(final Throwable ex,
    final IErrorCode serviceUnavailableCode) {

    // 1. Servicio Caído (Connection Refused, Timeout)
    if (ex instanceof WebClientRequestException) {
      log.error("Servicio externo no disponible: {}", ex.getMessage());
      return new BaseException(serviceUnavailableCode);
    }
    if (ex instanceof WebClientResponseException responseEx) {
      log.warn("Respuesta de error del servicio upstream: {}", responseEx.getResponseBodyAsString());
      return extractUpstreamException(responseEx);
    }
    log.error("Error inesperado en comunicación WebClient", ex);
    return new BaseException(BaseErrorMessage.ERROR_INTERNAL);
  }

  private BaseException extractUpstreamException(final WebClientResponseException ex) {
    try {
      GenericResponse<Map<String, Object>> errorResponse = this.objectMapper.readValue(
        ex.getResponseBodyAsString(),
        new TypeReference<>() {
        }
      );
      if (errorResponse != null && errorResponse.getBody() != null) {
        Map<String, Object> errorDetails = errorResponse.getBody();
        final Integer originalCode = (Integer) errorDetails.get("code");
        final String originalMessage = (String) errorDetails.get("message");
        final HttpStatus httpStatus = HttpStatus.valueOf(ex.getStatusCode().value());
        return new BaseException(DynamicErrorCode.fromOriginal(originalCode, originalMessage, httpStatus));
      }
    } catch (final Exception parseEx) {
      log.error("No se pudo parsear el error upstream", parseEx);
    }
    return new BaseException(ex.getStatusCode().is4xxClientError() ?
      BaseErrorMessage.NOT_FOUND : BaseErrorMessage.ERROR_INTERNAL);
  }
}
