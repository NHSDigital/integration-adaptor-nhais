FROM adoptopenjdk/openjdk11:ubi as build
WORKDIR /workspace/app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
RUN ./gradlew dependencies

COPY src src
RUN ./gradlew build -x test -x integrationTest
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

# FROM adoptopenjdk/openjdk11:ubi
FROM adoptopenjdk/openjdk11-openj9:jre
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","uk.nhs.digital.nhsconnect.nhais.IntegrationAdaptorNhaisApplication"]