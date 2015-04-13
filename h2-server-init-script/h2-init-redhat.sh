#!/bin/sh
#
# /etc/init.d/h2
# H2 database control script
#
# chkconfig: 345 87 13
# description: H2 database server
# pidfile: /var/run/h2/h2.pid
# config: /etc/default/h2.conf
#

### BEGIN INIT INFO
# Provides:          H2-Server
# Required-Start:
# Required-Stop:
# Default-Start:     3 5
# Default-Stop:      0 1 2 6
# Short-Description: H2-Server
# Description:       H2 database service
### END INIT INFO

# Load H2 database init.d configuration.
if [ -z "$H2_CONF" ]; then
    H2_CONF="/etc/default/h2.conf"
fi

[ -r "$H2_CONF" ] && . "${H2_CONF}"

# Set defaults.

if [ -z "$H2_HOME" ]; then
    H2_HOME=/opt/h2
fi
export H2_HOME

if [ -z "$H2_PIDFILE" ]; then
    H2_PIDFILE=$H2_HOME/bin/h2.pid
fi
export H2_PIDFILE

if [ -z "$H2_CONSOLE_LOG" ]; then
    H2_CONSOLE_LOG=$H2_HOME/log/h2.log
fi

if [ -z "$H2_BASEDIR" ]; then
    H2_BASEDIR=$H2_HOME/db
fi

if [ -z "$DATABASE" ]; then
    echo "Database is not set, set DATABASE variable"
    exit 1
fi

URL="jdbc:h2:tcp://localhost/$DATABASE;$H2_OPTS"

prog="H2 database"

start () {
     if [ -e $H2_PIDFILE ]; then
        echo "$prog is still running"
        exit 1
     fi

     cd $H2_HOME/bin

     java -cp h2*.jar $JVM_OPTS org.h2.tools.Server -tcp -baseDir $H2_BASEDIR > $H2_CONSOLE_LOG 2>&1 &

     echo $! > $H2_PIDFILE
     sleep 3
     echo "$prog started."
}

stop () {
     if [ -e $H2_PIDFILE ]; then
         PID=$(cat $H2_PIDFILE)
         kill -TERM ${PID}
         echo SIGTERM sent to process ${PID}
         rm $H2_PIDFILE
     else
         echo File $H2_PIDFILE not found!
     fi
}

status() {
    if [ -f $H2_PIDFILE ]; then
        PID=$(cat $H2_PIDFILE)
        if [ `ps --pid $PID 2> /dev/null | grep -c $PID 2> /dev/null` -eq '1' ]; then
            echo "$prog is running (pid $PID)"
            return 0
        else
            echo "$prog is dead but pid file exists"
            return 1
        fi
    fi
    echo "$prog is not running"
    return 3
}

backup () {
     SCRIPT="$DATABASE.$(date +"%Y%m%d%H%M%S").zip"
     echo "Backing up $prog to $SCRIPT"
     cd $H2_HOME/bin

     java -cp h2*.jar $JVM_OPTS org.h2.tools.Script -url $URL -user $USER -password $PASSWORD -script "$H2_BASEDIR/$SCRIPT" -options compression zip

}

restore () {
     SCRIPT=$1
     echo "Restoring $prog from $SCRIPT"
     cd $H2_HOME/bin

     java -cp h2*.jar $JVM_OPTS org.h2.tools.RunScript -url "$URL;create=true" -user $USER -password $PASSWORD -script "$H2_BASEDIR/$SCRIPT" -continueOnError -options compression zip

}

case "$1" in
    start)
      start
      ;;
    stop)
      stop
      ;;
    restart)
      stop
      sleep 5
      start
      ;;
    status)
      status
      ;;
    backup)
      backup
      ;;
    restore)
      restore $2
      ;;
    *)
      echo "Usage: /etc/init.d/h2 {start|stop|restart|status|backup|restore }"
      exit 1
      ;;
esac

exit 0
