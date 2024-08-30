package com.codecorecix.ecommerce.event.clients;

import com.codecorecix.ecommerce.maintenance.product.info.api.dto.response.ProductResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "APPMIC-E-CommercePlugins-maintenance", url = "localhost:9090")
public interface MaintenanceClientRest {

  @GetMapping("/api/product/getById/{id}")
  ResponseEntity<GenericResponse<ProductResponseDto>> getProductById(@PathVariable(value = "id") final Integer id);

}
