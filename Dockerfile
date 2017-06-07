FROM frolvlad/alpine-oraclejdk8:slim
MAINTAINER "A.Alexandrakis <a.alexandrakis@datamation.gr>"
# Define working directory.
WORKDIR /work
ADD target/direct-debit-gateway-0.0.1-SNAPSHOT.jar /work/direct-debit-gateway-0.0.1-SNAPSHOT.jar
# Expose Ports
EXPOSE 8080
#EXPOSE 8443
ENTRYPOINT exec java $JAVA_OPTS -jar /work/direct-debit-gateway-0.0.1-SNAPSHOT.jar --spring.config.location=/properties/application.properties