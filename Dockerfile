ARG GRADLE_BUILD_FILENAME=build.gradle

FROM gradle:jdk11 as cache
ARG GRADLE_BUILD_FILENAME
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY $GRADLE_BUILD_FILENAME /home/gradle/src/build.gradle
WORKDIR /home/gradle/src
RUN gradle clean build -i --stacktrace

FROM gradle:jdk11 AS build
ARG GRADLE_BUILD_FILENAME
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle --no-daemon -b $GRADLE_BUILD_FILENAME bootJar -i --stacktrace

FROM adoptopenjdk/openjdk11-openj9:jre

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/integration-adaptor-nhais.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/integration-adaptor-nhais.jar"]