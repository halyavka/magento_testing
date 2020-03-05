FROM gradle:5.0-jre8-alpine

USER root

RUN  mkdir -p /opt/xunit /opt/jenkins \
    && chown gradle:gradle /opt/xunit /opt/jenkins

USER gradle

ADD --chown=gradle:gradle build/docker /home/gradle/tests
WORKDIR /home/gradle/tests

ENTRYPOINT ["gradle", "--no-daemon", "--stacktrace", "-x", "classes", "--max-workers", "8", "-x", "testClasses", "--offline"]
CMD ["test"]
