package com.codecorecix.ecommerce.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignAuthInterceptor implements RequestInterceptor {

  @Override
  public void apply(final RequestTemplate requestTemplate) {
    final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
      return;
    }
    final HttpServletRequest request = servletRequestAttributes.getRequest();
    final String token = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (token != null && !token.isBlank()) {
      requestTemplate.header(HttpHeaders.AUTHORIZATION, token);
    }
  }
}

