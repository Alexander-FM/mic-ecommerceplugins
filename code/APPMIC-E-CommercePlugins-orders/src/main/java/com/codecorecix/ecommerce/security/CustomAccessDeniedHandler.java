package com.codecorecix.ecommerce.security;

import java.io.IOException;
import java.io.OutputStream;

import com.codecorecix.ecommerce.utils.GenericResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
    AccessDeniedException accessDeniedException) throws IOException {

    // Creamos tu objeto de respuesta personalizada
    GenericResponse<Object> genericResponse = new GenericResponse<>();
    genericResponse.setRpta(-1);
    genericResponse.setMessage("No tienes permisos suficientes para realizar esta operación.");
    genericResponse.setBody(accessDeniedException.getMessage());

    // Configuramos el HTTP Response
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403

    // Convertimos el objeto a JSON y lo escribimos en el body
    OutputStream out = response.getOutputStream();
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(out, genericResponse);
    out.flush();
  }
}