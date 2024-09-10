package com.codecorecix.ecommerce.event.clients;

import java.util.List;

import com.codecorecix.ecommerce.event.models.ProductInfo;
import com.codecorecix.ecommerce.utils.GenericResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "APPMIC-E-CommercePlugins-maintenance", url = "http://localhost:9090")
public interface MaintenanceClientRest {

  @GetMapping("/api/product/checkProducts")
  GenericResponse<List<ProductInfo>> checkProducts(@RequestParam final List<Integer> ids);

}
