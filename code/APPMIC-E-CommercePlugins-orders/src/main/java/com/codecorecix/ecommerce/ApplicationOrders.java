package com.codecorecix.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;

@EnableDiscoveryClient
@EnableFeignClients
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@SpringBootApplication
public class ApplicationOrders {

  public static void main(String[] args) {
    SpringApplication.run(ApplicationOrders.class, args);
  }

}
