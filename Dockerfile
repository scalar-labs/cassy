FROM openjdk:8u212-jdk-stretch AS builder

RUN mkdir -p /opt/cassandra-backup

COPY . /opt/cassandra-backup

WORKDIR /opt/cassandra-backup

RUN ./gradlew installDist

# Runtime Container
FROM openjdk:8-alpine

RUN apk add sqlite

# Copy Build output from Builder Step
COPY --from=builder /opt/cassandra-backup/build /opt/cassandra-backup/build
COPY --from=builder /opt/cassandra-backup/scripts /opt/cassandra-backup/scripts
COPY conf/backup-server.properties /opt/cassandra-backup

WORKDIR /opt/cassandra-backup

RUN sqlite3 ./cassy.db < ./scripts/db.schema
VOLUME /opt/cassandra-backup/conf

ENTRYPOINT ["/bin/sh", "scripts/entrypoint.sh"]
CMD ["--config", "conf/backup-server.properties"]

EXPOSE 20051
