FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY ./target/APPMIC-E-CommercePlugins-gateway-2.10.1-SNAPSHOT.jar .

EXPOSE 80

ENTRYPOINT ["java", "-jar", "APPMIC-E-CommercePlugins-gateway-2.10.1-SNAPSHOT.jar"]