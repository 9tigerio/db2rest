FROM bellsoft/liberica-runtime-container:jre-21-cds-slim-musl

# Sets default version of db2rest to download during ADD
# Can be overridden during build with option like `--build-arg DB2REST_VERSION=1.2.4`
ARG DB2REST_VERSION=1.2.4

# Adds remote db2rest jar into /opt/app/db2rest.jar
ADD https://search.maven.org/remotecontent?filepath=io/9tiger/db2rest/$DB2REST_VERSION/db2rest-$DB2REST_VERSION.jar /opt/app/db2rest.jar

# cd /opt/app
WORKDIR /opt/app

# uncomment EXPOSE if you wish to automatically expose
# port 8080 (default service port) upon container start
# otherwise you can map the port on docker run with `-p 1234:8080`

# EXPOSE 8080

# java -jar /opt/app/db2rest.jar
ENTRYPOINT ["java","-jar","db2rest.jar"]
