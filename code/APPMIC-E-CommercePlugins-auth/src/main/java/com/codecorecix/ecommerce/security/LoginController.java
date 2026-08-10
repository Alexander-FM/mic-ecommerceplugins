package com.codecorecix.ecommerce.security;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

  /**
   * Evita el error 500 en la raíz.
   * Muestra el mensaje de activación directamente en el navegador.
   */
  @RequestMapping(
      value = "/",
      method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
  )
  public ResponseEntity<String> home() {
    // Al usar ResponseEntity.ok(), Spring sabe que es texto plano y no un archivo HTML
    return ResponseEntity.ok("The microservice APPMIC-E-CommercePlugins-auth has been activated");
  }

  /**
   * Redirecciona a una página personalizada para el login.
   *
   * @return La vista personalizada para el login.
   */
  @GetMapping("/login")
  public String login() {
    return "login";
  }
}

