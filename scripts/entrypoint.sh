#!/bin/sh

if [ ! -f /etc/cassy/data/cassy.db ]; then
   cp cassy.db /etc/cassy/data/;
fi;

if [ ! -f /etc/cassy/conf/backup-server.properties ]; then
   cp conf/backup-server.properties /etc/cassy/conf/;
fi;

build/install/cassy/bin/backup-server $@
