FROM bellsoft/liberica-runtime-container:jre-21-cds-slim-musl

ARG JAR_FILE=target/db2rest-*.jar

COPY ${JAR_FILE} /opt/app/db2rest.jar

# cd /opt/app
WORKDIR /opt/app

# uncomment EXPOSE if you wish to automatically expose
# port 8080 (default service port) upon container start
# otherwise you can map the port on docker run with `-p 1234:8080`

# EXPOSE 8080

# java -jar /opt/app/db2rest.jar
ENTRYPOINT ["java","-jar","db2rest.jar"]
