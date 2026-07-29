# =================================================================
# ETAPA 1: BUILDER
# =================================================================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 1. Copia el POM padre y los POM de TODOS los módulos.
# Esto es crucial para que Maven entienda la estructura del proyecto.
COPY pom.xml .
COPY APPMIC-E-CommercePlugins-auth/pom.xml       APPMIC-E-CommercePlugins-auth/
COPY APPMIC-E-CommercePlugins-common/pom.xml     APPMIC-E-CommercePlugins-common/
COPY APPMIC-E-CommercePlugins-gateway/pom.xml    APPMIC-E-CommercePlugins-gateway/
COPY APPMIC-E-CommercePlugins-maintenance/pom.xml APPMIC-E-CommercePlugins-maintenance/
COPY APPMIC-E-CommercePlugins-orders/pom.xml     APPMIC-E-CommercePlugins-orders/
# Añade aquí cualquier otro módulo que falte
COPY APPMIC-E-CommercePlugins-alerts/pom.xml     APPMIC-E-CommercePlugins-alerts/
COPY APPMIC-E-CommercePlugins-reports/pom.xml    APPMIC-E-CommercePlugins-reports/
COPY APPMIC-E-CommercePlugins-returns/pom.xml    APPMIC-E-CommercePlugins-returns/
COPY APPMIC-E-CommercePlugins-tracking/pom.xml   APPMIC-E-CommercePlugins-tracking/


# 2. Descarga las dependencias (aprovechando la caché de Docker)
# Ahora Maven puede resolver el proyecto completo.
RUN mvn dependency:go-offline

# 3. Copia TODO el código fuente.
# El .dockerignore en la raíz se encargará de excluir 'target', '.git', etc.
COPY . .

# 4. Construye solo el módulo 'auth' y sus dependencias necesarias (-am)
RUN mvn -pl APPMIC-E-CommercePlugins-auth -am package -DskipTests


# =================================================================
# ETAPA 2: FINAL
# =================================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 5. Copia el JAR final desde la ruta correcta dentro de la etapa 'builder'
COPY --from=builder /app/APPMIC-E-CommercePlugins-auth/target/APPMIC-E-CommercePlugins-auth-2.10.1-SNAPSHOT.jar .

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "APPMIC-E-CommercePlugins-auth-2.10.1-SNAPSHOT.jar"]