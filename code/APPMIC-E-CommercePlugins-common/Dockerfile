FROM openjdk:21

WORKDIR /app

COPY ./target/APPMIC-E-CommercePlugins-common-2.10.1-SNAPSHOT.jar .

EXPOSE 9089

ENTRYPOINT ["java", "-jar", "APPMIC-E-CommercePlugins-common-2.10.1-SNAPSHOT.jar"]