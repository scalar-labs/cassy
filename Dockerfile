FROM openjdk:8u212-jdk-stretch AS builder

RUN mkdir -p /opt/cassy
WORKDIR /opt/cassy

COPY gradle ./gradle
COPY build.gradle ./
COPY gradlew ./gradlew
COPY src ./src

RUN ./gradlew installDist

# Runtime Container
FROM openjdk:8-alpine

RUN apk add sqlite

WORKDIR /opt/cassy

RUN mkdir -p /cassy/data && mkdir -p /cassy/conf

# Copy Build output from Builder Step
COPY --from=builder /opt/cassy/build ./build
COPY scripts ./scripts
COPY conf ./conf

RUN sqlite3 ./cassy.db < ./scripts/db.schema

VOLUME /cassy

ENTRYPOINT ["/bin/sh", "scripts/entrypoint.sh"]
CMD ["--config", "/cassy/conf/cassy.properties"]

EXPOSE 20051
