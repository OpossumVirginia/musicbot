FROM openjdk:20

#required for the external run configuration:
#https://www.jetbrains.com/help/idea/dockerfile-run-configuration.html
ARG env=dev

ENV ENVIRONMENT=$env

#passed when implementing a pipeline
#ARG GIT_COMMIT

LABEL maintainer="opossum"

EXPOSE 8080:8080

COPY build/libs/musicbot-0.0.1-SNAPSHOT.jar app.jar
COPY build/resources/main/application-$env.properties application-$env.properties
COPY build/resources/main/logback-spring.xml /src/main/resources/logback-spring.xml

ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=${ENVIRONMENT}"]

#valid when in pipeline
#RUN echo "Based on commit: $GIT_COMMIT"