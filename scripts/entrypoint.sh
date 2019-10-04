#!/bin/sh

if [ ! -f /cassy/data/cassy.db ]; then
   cp cassy.db /cassy/data/;
fi;

if [ ! -f /cassy/conf/cassy.properties ]; then
   cp conf/cassy.properties /cassy/conf/;
fi;

build/install/cassy/bin/cassy-server $@
