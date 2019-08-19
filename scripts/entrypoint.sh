#!/bin/sh

if [ ! -f conf/cassy.db ]; then
   cp cassy.db conf/cassy.db;
fi;

if [ ! -f conf/backup-server.properties ]; then
   cp backup-server.properties conf/backup-server.properties;
fi;

build/install/cassandra-backup/bin/backup-server $@
