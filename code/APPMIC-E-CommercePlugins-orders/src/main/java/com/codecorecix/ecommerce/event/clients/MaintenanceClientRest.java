package com.codecorecix.ecommerce.event.clients;

import java.util.Collections;
import java.util.List;

import com.codecorecix.ecommerce.event.models.ProductInfo;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericResponseConstants;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${app.external.maintenance-service-url}", url = "${app.external.maintenance-service-url}")
public interface MaintenanceClientRest {

  @GetMapping("/api/products/checkProducts")
  @CircuitBreaker(name = "maintenanceService", fallbackMethod = "fallbackCheckProducts")
  GenericResponse<List<ProductInfo>> checkProducts(@RequestParam final List<Integer> ids,
    @RequestHeader(value = "Authorization") final String token);

  default GenericResponse<List<ProductInfo>> fallbackCheckProducts(List<Integer> ids, Throwable throwable) {
    if (throwable instanceof feign.FeignException.NotFound notFound) {
      throw notFound;
    }
    if (throwable instanceof FeignException.Forbidden forbidden) {
      throw forbidden;
    }
    if (throwable instanceof FeignException.Unauthorized unauthorized) {
      throw unauthorized;
    }
    return new GenericResponse<>(GenericResponseConstants.RPTA_ERROR, GenericResponseConstants.UNAVAILABLE_SERVICE,
      Collections.emptyList());
  }
}