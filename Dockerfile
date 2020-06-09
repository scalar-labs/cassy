FROM openjdk:8u212-jre-slim-stretch

WORKDIR /cassy

# The Docker context is created in build/docker by `./gradlew docker`
COPY ./cassy.tar .
RUN tar xf cassy.tar -C / && rm -f cassy.tar

COPY scripts ./scripts

RUN apt-get update && apt-get install -y \
    sqlite \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /cassy/data \
    && sqlite3 /cassy/data/cassy.db < ./scripts/db.schema

RUN mkdir -p /cassy/conf

CMD ["/cassy/bin/cassy-server", "--config", "/cassy/conf/cassy.properties"]

EXPOSE 20051
