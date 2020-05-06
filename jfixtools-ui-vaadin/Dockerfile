FROM bellsoft/liberica-openjdk-alpine:11-armv7l
ARG JAR_FILE=target/jfixtools-ui-vaadin-1.0-BETA.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]