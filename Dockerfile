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

RUN mkdir -p /etc/cassy/data && mkdir -p /etc/cassy/conf

# Copy Build output from Builder Step
COPY --from=builder /opt/cassy/build ./build
COPY scripts ./scripts
COPY conf ./conf

RUN sqlite3 ./cassy.db < ./scripts/db.schema

VOLUME /etc/cassy

ENTRYPOINT ["/bin/sh", "scripts/entrypoint.sh"]
CMD ["--config", "/etc/cassy/conf/backup-server.properties"]

EXPOSE 20051
