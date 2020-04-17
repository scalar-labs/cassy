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

RUN wget https://github.com/fullstorydev/grpcurl/releases/download/v1.1.0/grpcurl_1.1.0_linux_x86_64.tar.gz

RUN tar -xvzf grpcurl_1.1.0_linux_x86_64.tar.gz
RUN chmod +x grpcurl
RUN mv grpcurl /usr/local/bin/grpcurl
RUN grpcurl --help
RUN apk add sqlite

WORKDIR /opt/cassy

RUN mkdir -p /cassy/data && mkdir -p /cassy/conf && mkdir -p /cassy/.ssh && mkdir -p /cassy/.aws

# Copy Build output from Builder Step
COPY --from=builder /opt/cassy/build ./build
COPY scripts ./scripts
COPY conf ./conf
COPY .ssh ./.ssh
COPY .aws ./.aws

RUN sqlite3 ./cassy.db < ./scripts/db.schema

VOLUME /cassy

ENTRYPOINT ["/bin/sh", "scripts/entrypoint.sh"]
CMD ["--config", "/cassy/conf/cassy.properties"]

EXPOSE 20051
