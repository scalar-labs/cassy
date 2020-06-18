FROM openjdk:8u212-jre-slim-stretch

WORKDIR /cassy

# The Docker context is created in build/docker by `./gradlew docker`
COPY ./cassy.tar .
RUN tar xf cassy.tar -C / && rm -f cassy.tar

COPY scripts ./scripts
COPY logback.xml entrypoint.sh ./

RUN apt-get update && apt-get install -y \
    sqlite3 \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /cassy/data /cassy/conf

ENV JAVA_OPTS -Dlogback.configurationFile=/cassy/logback.xml
ENTRYPOINT ["/cassy/entrypoint.sh"]
CMD ["/cassy/bin/cassy-server", "--config", "/cassy/conf/cassy.properties"]

EXPOSE 20051
