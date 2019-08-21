#!/bin/sh

if [ ! -f /cassy/data/cassy.db ]; then
   cp cassy.db /cassy/data/;
fi;

if [ ! -f /cassy/conf/backup-server.properties ]; then
   cp conf/backup-server.properties /cassy/conf/;
fi;

build/install/cassy/bin/backup-server $@
