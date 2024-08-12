package com.codecorecix.ecommerce.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenericResponseConstants {

  public static final String TIPO_EXCEPTION = "exception";

  public static final String TIPO_SQL_EXCEPTION = "sql-exception";

  public static final String TIPO_VALID_EXCEPTION = " valid-exception";

  public static final String TIPO_RESULT = "result";

  public static final String TIPO_DATA = "data";

  public static final String TIPO_AUTH = "auth";

  public static final int RPTA_OK = 1;

  public static final int RPTA_WARNING = 0;

  public static final int RPTA_ERROR = -1;

  public static final String OPERACION_CORRECTA = "Operación finalizada correctamente";

  public static final String OPERACION_INCORRECTA = "No se ha podido culminar la operación";

  public static final String OPERACION_ERRONEA = "Ha ocurrido un error al realizar la operación";

  public static final String RESOURCES_NOT_FOUND = "No se encontró el archivo";

  public static final String ORIGINAL_URL = "https://drive.google.com/file/d/";

  public static final String VIEW = "/view";

  public static final String DASH = " - ";

  public static final String COLON = ": ";

  public static final String PERIOD = ".";

  public static final String COMMA = ",";
}
