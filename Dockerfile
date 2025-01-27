### BUILD image
FROM maven:3-openjdk-17-slim as builder

# create app folder for sources
RUN mkdir -p /build
WORKDIR /build

#Copy source code
COPY . /build/

# Build application
RUN mvn clean package

FROM openjdk:17-slim as runtime

EXPOSE 8086

#Set app home folder
ENV APP_HOME /app

#Possibility to set JVM options (https://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html)
ENV JAVA_OPTS=""

#Create base app folder
RUN mkdir $APP_HOME

#Create folder to save configuration files
RUN mkdir $APP_HOME/config

#Create folder with application logs
RUN mkdir $APP_HOME/log

VOLUME $APP_HOME/log
VOLUME $APP_HOME/config

WORKDIR $APP_HOME

#Copy executable jar file from the builder image
COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar" ]
