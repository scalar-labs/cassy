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

# Copy Build output from Builder Step
COPY --from=builder /opt/cassy/build ./build
COPY scripts ./scripts
COPY conf ./conf
COPY .ssh ./.ssh
COPY .aws ./.aws
COPY cassy.db ./cassy.db

ENV AWS_CONFIG_FILE=./.aws/config
#RUN sqlite3 ./cassy.db < ./scripts/db.schema

VOLUME /cassy

RUN wget https://archive.apache.org/dist/cassandra/3.11.3/apache-cassandra-3.11.3-bin.tar.gz \
    && tar -xzvf apache-cassandra-3.11.3-bin.tar.gz \
    && rm -rf apache-cassandra-3.11.3-bin.tar.gz

EXPOSE 7000 7001 7199 9042 9160
CMD ["./apache-cassandra-3.11.3/bin/cassandra", "-R"]

ENTRYPOINT ["/bin/sh", "scripts/entrypoint.sh"]
CMD ["--config", "/cassy/conf/cassy.properties"]

EXPOSE 20051
