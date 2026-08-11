package com.codecorecix.ecommerce.services;

import java.util.List;

import com.codecorecix.ecommerce.api.dto.request.RegisterRequestDto;
import com.codecorecix.ecommerce.client.MaintenanceClient;
import com.codecorecix.ecommerce.event.models.CustomerRequestDto;
import com.codecorecix.ecommerce.event.models.CustomerResponseDto;
import com.codecorecix.ecommerce.event.models.RoleRequestDto;
import com.codecorecix.ecommerce.event.models.RoleResponseDto;
import com.codecorecix.ecommerce.event.models.UserRequestDto;
import com.codecorecix.ecommerce.event.models.UserResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

  private final MaintenanceClient maintenanceClient;

  public CustomerResponseDto registerUserAndCustomer(final RegisterRequestDto request) {

    UserResponseDto createdUser = null;
    try {
      // 1. Obtener rol USER
      GenericResponse<RoleResponseDto> roleResponse = maintenanceClient.findRoleByName("USER");
      if (roleResponse.getRpta() != 1) {
        throw new RuntimeException("No se pudo encontrar el rol USER.");
      }

      // 2. Crear usuario
      UserRequestDto userRequest = new UserRequestDto();
      userRequest.setUsername(request.getUsername());
      userRequest.setPassword(request.getPassword());
      userRequest.setIsActive(true);
      // ... (setear username, password, etc. con el rol obtenido)
      userRequest.setRoles(List.of(new RoleRequestDto(roleResponse.getBody().getId(), roleResponse.getBody().getDescription(),
          roleResponse.getBody().getIsActive())));

      GenericResponse<UserResponseDto> userResponse = maintenanceClient.createUser(userRequest);
      if (userResponse.getRpta() != 1) {
        throw new RuntimeException("Error al crear usuario: " + userResponse.getMessage());
      }
      createdUser = userResponse.getBody();
      log.info("Usuario creado temporalmente con ID: {}", createdUser.getId());

      // 3. Crear cliente
      CustomerRequestDto customerRequest = request.getCustomer();
      customerRequest.setUserId(createdUser.getId());

      GenericResponse<CustomerResponseDto> customerResponse = maintenanceClient.createCustomer(customerRequest);
      if (customerResponse.getRpta() != 1) {
        throw new RuntimeException("Error al crear cliente: " + customerResponse.getMessage());
      }

      log.info("Cliente creado con éxito. Registro completado.");
      return customerResponse.getBody();

    } catch (final Exception ex) {
      // --- LÓGICA DE COMPENSACIÓN ---
      log.error("Error durante el registro: {}. Iniciando compensación.", ex.getMessage());
      if (createdUser != null) {
        try {
          maintenanceClient.deleteUserInternal(createdUser.getId());
          log.warn("Usuario huérfano con ID: {} eliminado por compensación.", createdUser.getId());
        } catch (final Exception compEx) {
          log.error("¡FALLO CRÍTICO! La compensación falló. El usuario con ID {} debe ser eliminado manualmente.", createdUser.getId(),
              compEx);
        }
      }
      // Propagamos la excepción original para que el controlador la maneje
      throw ex;
    }
  }
}
