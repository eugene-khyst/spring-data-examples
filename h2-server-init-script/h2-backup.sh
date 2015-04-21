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
    echo "Usage: h2-backup.sh URL USER PASSWORD"
    exit 1
fi

if [ -z "$USER" ]; then
    echo "USER is not set"
    echo "Usage: h2-backup.sh URL USER PASSWORD"
    exit 1
fi

if [ -z "$PASSWORD" ]; then
    echo "PASSWORD is not set"
    echo "Usage: h2-backup.sh URL USER PASSWORD"
    exit 1
fi

if [ -z "$SCRIPT" ]; then
    SCRIPT="$(date +"%Y%m%d%H%M%S").zip"
    echo "SCRIPT is not set"
    echo "Will use '$SCRIPT' as SCRIPT"
fi

echo "Backing up H2 database $URL to $SCRIPT"

java -cp $H2_HOME/bin/h2*.jar org.h2.tools.Script -url $URL -user $USER -password $PASSWORD -script $SCRIPT -options compression zip
