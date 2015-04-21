#!/bin/sh

if [ -z "$H2_HOME" ]; then
    echo "H2_HOME is not set"
    echo "Set H2_HOME environment variable: export H2_HOME=/path/to/h2"
    exit 1
fi

URL=$1
USER=$2
PASSWORD=$3
SCRIPT=$4

if [ -z "$URL" ]; then
    echo "URL is not set"
    echo "Usage: h2-restore.sh URL USER PASSWORD SCRIPT"
    exit 1
fi

if [ -z "$USER" ]; then
    echo "USER is not set"
    echo "Usage: h2-restore.sh URL USER PASSWORD SCRIPT"
    exit 1
fi

if [ -z "$PASSWORD" ]; then
    echo "PASSWORD is not set"
    echo "Usage: h2-restore.sh URL USER PASSWORD SCRIPT"
    exit 1
fi

if [ -z "$SCRIPT" ]; then
    echo "SCRIPT is not set"
    echo "Usage: h2-restore.sh URL USER PASSWORD SCRIPT"
    exit 1
fi

echo "Restoring H2 database from $SCRIPT"

java -cp $H2_HOME/bin/h2*.jar org.h2.tools.RunScript -url $URL -user $USER -password $PASSWORD -script $SCRIPT -continueOnError -options compression zip