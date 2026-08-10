package com.codecorecix.ecommerce.controller;

import com.codecorecix.ecommerce.api.dto.request.RegisterRequestDto;
import com.codecorecix.ecommerce.event.models.CustomerResponseDto;
import com.codecorecix.ecommerce.services.RegistrationService;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.GenericUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.endpoints.auth}")
@RequiredArgsConstructor
public class AuthController {

  private final RegistrationService registrationService;

  @PostMapping("/register")
  public ResponseEntity<GenericResponse<CustomerResponseDto>> register(@RequestBody RegisterRequestDto request) {
    try {
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(GenericUtils.buildGenericResponseSuccess("Registro exitoso", this.registrationService.registerUserAndCustomer(request)));
    } catch (final Exception ex) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(GenericUtils.buildGenericResponseError("Error en el registro", null));
    }
  }
}