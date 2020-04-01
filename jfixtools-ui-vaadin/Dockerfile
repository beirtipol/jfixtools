FROM openjdk:11-jre
ARG JAR_FILE=target/jfixtools-ui-vaadin-1.0-BETA.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]