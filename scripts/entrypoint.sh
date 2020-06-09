#!/bin/sh

if [ ! -f /cassy/data/cassy.db ]; then
  sqlite3 /cassy/data/cassy.db < /cassy/scripts/db.schema
fi

exec "$@"
