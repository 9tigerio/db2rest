FROM bellsoft/liberica-runtime-container:jre-21-cds-slim-musl


# Refer to Maven build -> finalName
ARG JAR_FILE=target/db2rest.jar

# cd /opt/app
WORKDIR /opt/app

# cp target/db2rest.jar /opt/app/db2rest.jar
COPY ${JAR_FILE} db2rest.jar

# java -jar /opt/app/db2rest.jar
ENTRYPOINT ["java","-jar","db2rest.jar"]
