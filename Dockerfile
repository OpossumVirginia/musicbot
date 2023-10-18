FROM openjdk:20

ENV ENVIRONMENT=DEV

ARG GIT_COMMIT

LABEL maintainer="opossum"

EXPOSE 8080

#TODO:the deployment in container does not work for now!!!!
#ADD backend/target/capstone.jar app.jar

#CMD [ "sh", "-c", "java -jar /app.jar" ]

#RUN echo "Based on commit: $GIT_COMMIT"