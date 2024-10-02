package com.codecorecix.ecommerce.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.springframework.util.StringUtils;

public class TestUtils {

  private static final String NOW_PLUS = "now+";

  private static final String NOW_MINUS = "now-";

  private static final String NOW_REGEX = "now\\+";

  public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  public static <T> T getResource(String resource, Class<T> targetClass, Class<?> testClass) throws IOException {
    String path = getResourcePath(resource, testClass);
    ObjectMapper objectMapper = new ObjectMapper();
    registerCustomDeserializers(objectMapper);
    return objectMapper.readValue(testClass.getResource(path), targetClass);
  }

  public static <T> T getResource(String resource, Class<T> targetClass, Class<?> testClass, ObjectMapper objectMapper) throws IOException {
    String path = getResourcePath(resource, testClass);
    registerCustomDeserializers(objectMapper);
    return objectMapper.readValue(testClass.getResource(path), targetClass);
  }

  public static <T> T getResource(String resource, TypeReference<T> targetTypeRef, Class<?> testClass) throws IOException {
    String path = getResourcePath(resource, testClass);
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(testClass.getResource(path), targetTypeRef);
  }

  public static String getResourceAsString(String resource, Class<?> testClass) throws IOException {
    String path = getResourcePath(resource, testClass);
    File file = new File(Objects.requireNonNull(testClass.getResource(path)).getPath());
    return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
  }

  public static <T> T getXmlResourceJakarta(String resource, Class<T> targetClass, Class<?> currentClass)
      throws IOException, JAXBException {
    String path = getResourcePath(resource, currentClass);
    URL url = currentClass.getResource(path);
    if (url == null) {
      throw new IOException("Resource not found: " + path);
    }
    JAXBContext jaxbContext = JAXBContext.newInstance(targetClass);
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    Object result = unmarshaller.unmarshal(url);
    if (targetClass.isInstance(result)) {
      return targetClass.cast(result);
    } else {
      throw new JAXBException("El XML no se pudo deserializar en el tipo esperado: " + targetClass.getName());
    }
  }

  public static byte[] getResourceAsBytes(String resource, Class<?> testClass) throws IOException {
    String path = getResourcePath(resource, testClass);
    File file = new File(Objects.requireNonNull(testClass.getResource(path)).getPath());
    return FileUtils.readFileToByteArray(file);
  }

  private static void registerCustomDeserializers(ObjectMapper objectMapper) {
    SimpleModule simpleModule = new SimpleModule();
    simpleModule.addDeserializer(OffsetDateTime.class,
        createDeserializer(text -> OffsetDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
    simpleModule.addDeserializer(LocalDateTime.class, createDeserializer(text -> LocalDateTime.parse(text, DATETIME_FORMATTER)));
    simpleModule.addDeserializer(ZonedDateTime.class, createDeserializer(text -> ZonedDateTime.parse(text, DATETIME_FORMATTER)));
    simpleModule.addDeserializer(Instant.class, createInstantDeserializer(Instant::parse));
    simpleModule.addDeserializer(ObjectId.class, createDeserializer(ObjectId::new));
    objectMapper.registerModule(simpleModule);
  }

  private static <T> JsonDeserializer<T> createDeserializer(final Function<String, T> parsingFunction) {
    return new JsonDeserializer<>() {
      public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return parsingFunction.apply(parser.getText());
      }
    };
  }

  private static <T> JsonDeserializer<T> createInstantDeserializer(final Function<String, T> parsingFunction) {
    return new JsonDeserializer<>() {
      @Override
      public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String inputText = parser.getText();
        Instant now = Instant.now();
        if (inputText.startsWith(NOW_PLUS)) {
          long days = Long.parseLong(inputText.split(NOW_REGEX)[1]);
          return parsingFunction.apply(TestUtils.getInputDateAsString(now::plus, days));
        } else if (inputText.startsWith(NOW_MINUS)) {
          long days = Long.parseLong(inputText.split(NOW_MINUS)[1]);
          return parsingFunction.apply(TestUtils.getInputDateAsString(now::minus, days));
        } else {
          return parsingFunction.apply(inputText);
        }
      }
    };
  }

  public static String getInputDateAsString(final BiFunction<Long, TemporalUnit, Instant> operation, final Long days) {
    return DATETIME_FORMATTER.withZone(ZoneOffset.UTC).format(operation.apply(days, ChronoUnit.DAYS));
  }

  public static String getResourcePath(String resource, Class<?> testClass) throws IOException {
    int dots = StringUtils.countOccurrencesOf(resource, ".");
    if (dots > 1) {
      throw new FileNotFoundException();
    } else {
      String path;
      if (resource.contains("/")) {
        path = resource.startsWith("/") ? resource : "/".concat(resource);
      } else {
        String packagePath = testClass.getPackage().getName();
        path = getValidPackagePath(packagePath).concat(resource);
      }

      return path;
    }
  }

  public static <T> List<T> getListResource(String resource, Class<T> targetClass, Class<?> testClass) throws IOException {
    String path = getResourcePath(resource, testClass);
    ObjectMapper objectMapper = new ObjectMapper();
    registerCustomDeserializers(objectMapper);
    return objectMapper.readValue(testClass.getResource(path),
        objectMapper.getTypeFactory().constructCollectionType(List.class, targetClass));
  }

  public static <T> List<T> getListResource(String resource, Class<T> targetClass, Class<?> testClass, ObjectMapper objectMapper)
      throws IOException {
    String path = getResourcePath(resource, testClass);
    registerCustomDeserializers(objectMapper);
    return objectMapper.readValue(testClass.getResource(path),
        objectMapper.getTypeFactory().constructCollectionType(List.class, targetClass));
  }

  private static String getValidPackagePath(String packagePath) {
    String packageValidName = StringUtils.replace(packagePath, ".", "/");
    return String.format("/%s/", packageValidName);
  }

  private TestUtils() {
  }
}
