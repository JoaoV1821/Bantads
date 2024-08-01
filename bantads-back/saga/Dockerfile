FROM openjdk:17
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/saga-0.0.1-SNAPSHOT.jar saga.jar
EXPOSE 8084
#ENTRYPOINT exec java $JAVA_OPTS -jar saga.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspringamqp.deserialization.trust.all -jar saga.jar
