FROM openjdk:17
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/conta-0.0.1-SNAPSHOT.jar conta.jar
EXPOSE 8081
#ENTRYPOINT exec java $JAVA_OPTS -jar saga.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspringamqp.deserialization.trust.all -jar conta.jar
