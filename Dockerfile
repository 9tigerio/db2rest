FROM bellsoft/liberica-runtime-container:jre-21-cds-slim-musl


# Refer to Maven build -> finalName
ARG JAR_FILE=target/db2rest.jar

# cd /opt/app
WORKDIR /opt/app

# cp target/db2rest.jar /opt/app/db2rest.jar
COPY ${JAR_FILE} db2rest.jar

# uncomment EXPOSE if you wish to automatically expose
# port 8080 (default service port) upon container start
# otherwise you can map the port on docker run with `-p 1234:8080`

# EXPOSE 8080

# java -jar /opt/app/db2rest.jar
ENTRYPOINT ["java","-jar","db2rest.jar"]
