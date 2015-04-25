#!/bin/sh
#
# /etc/init.d/h2
# H2 database control script
#
# chkconfig: 345 87 13
# description: H2 database startup script
# processname: h2
# pidfile: /var/run/h2/h2.pid
# config: /etc/default/h2.conf
#

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
    H2_PIDFILE=/var/run/h2/h2.pid
fi
export H2_PIDFILE

if [ -z "$H2_CONSOLE_LOG" ]; then
    H2_CONSOLE_LOG=/var/log/h2/console.log
fi

if [ -z "$H2_BASEDIR" ]; then
    H2_BASEDIR=$H2_HOME/db
fi

if [ -z "$H2_USER" ]; then
    H2_USER=h2
fi

prog="H2 database"

start () {
     if [ -f $H2_PIDFILE ]; then
        PID=$(cat $H2_PIDFILE)
        if [ `(ps --pid $PID 2> /dev/null | grep -c $PID 2> /dev/null)` -eq '1' ]; then
            echo "$prog is already running (pid $PID)"
            return 1
        else
            rm -f $H2_PIDFILE
        fi
     fi
     mkdir -p $(dirname $H2_CONSOLE_LOG)
     cat /dev/null > $H2_CONSOLE_LOG

     mkdir -p $(dirname $H2_PIDFILE)
     chown $H2_USER $(dirname $H2_PIDFILE) || true

     su - $H2_USER -c "java -cp $H2_HOME/bin/h2*.jar $JVM_OPTS org.h2.tools.Server -tcp -baseDir $H2_BASEDIR" >> $H2_CONSOLE_LOG 2>&1 &

     echo $! > $H2_PIDFILE
     sleep 3
     echo "$prog started."
     return 0
}

stop () {
     if [ -f $H2_PIDFILE ]; then
         PID=$(cat $H2_PIDFILE)
         kill -TERM ${PID}
         echo "SIGTERM sent to process ${PID}"
         rm -f $H2_PIDFILE
     else
         echo "File $H2_PIDFILE not found!"
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
    *)
      echo "Usage: /etc/init.d/h2 {start|stop|restart|status}"
      exit 1
      ;;
esac

exit 0
