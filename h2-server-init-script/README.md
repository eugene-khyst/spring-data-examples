H2 Server Init Script
=====================

Installing H2 database as a service
-----------------------------------

Installing H2 database as a service on Linux has multiple advantages like automatic start on system boot, convinient management with `service` command and other.

To install H2 database as a service on CentOS and other RPM-based Linux distributions do steps described below.

Install Java

```
yum install java-1.8.0-openjdk
```

Create directories _bin_ and _db_ under _/opt/h2/_.

```
mkdir /opt/h2
mkdir /opt/h2/bin
mkdir /opt/h2/db
```

Download [H2 database JAR](http://central.maven.org/maven2/com/h2database/h2/1.4.187/h2-1.4.187.jar)

```
wget http://central.maven.org/maven2/com/h2database/h2/1.4.187/h2-1.4.187.jar
mv h2-1.4.187.jar /opt/h2/bin/
```

Copy [h2-init-redhat.sh](https://github.com/evgeniy-khist/examples/blob/master/h2-server-init-script/h2-init-redhat.sh) to _/etc/init.d/h2_

```
wget --no-check-certificate https://raw.githubusercontent.com/evgeniy-khist/examples/master/h2-server-init-script/h2-init-redhat.sh
cp h2-init-redhat.sh /etc/init.d/h2
```

Copy [h2.conf](https://github.com/evgeniy-khist/examples/blob/master/h2-server-init-script/h2.conf) to _/etc/default/h2.conf_

```
wget --no-check-certificate https://raw.githubusercontent.com/evgeniy-khist/examples/master/h2-server-init-script/h2.conf
cp h2.conf /etc/default/h2.conf
```

Edit file _/etc/default/h2.conf_ to change JVM options or other init script parameters

```
nano /etc/default/h2.conf
```

Make _/etc/init.d/h2_ executable

```
chmod +x /etc/init.d/h2
```

Add user to run H2 database

```
adduser h2
```

Chage the owner of H2 directories

```
chown -R h2:h2 /opt/h2
```

Add H2 database as a service

```
chkconfig --add h2
chkconfig h2 on
```

Start H2 database

```
service h2 start
```

H2 logs are saved in _/var/log/h2/console.log_

```
tail -f /var/log/h2/console.log
```

Backing up H2 database
----------------------

Copy [h2-backup.sh](https://github.com/evgeniy-khist/examples/blob/master/h2-server-init-script/h2-backup.sh) to _/opt/h2/bin_

```
wget --no-check-certificate https://raw.githubusercontent.com/evgeniy-khist/examples/master/h2-server-init-script/h2-backup.sh
cp h2-backup.sh $H2_HOME/bin
```

To backup H2 database

```
$H2_HOME/bin/h2-backup.sh jdbc:h2:tcp://localhost/test sa sa test.zip
```

Backup script will be located at _/opt/h2/bin/test.zip_.

Restoring H2 database
---------------------

Copy [h2-restore.sh](https://github.com/evgeniy-khist/examples/blob/master/h2-server-init-script/h2-restore.sh) to _/opt/h2/bin_

```
wget --no-check-certificate https://raw.githubusercontent.com/evgeniy-khist/examples/master/h2-server-init-script/h2-restore.sh
cp h2-restore.sh $H2_HOME/bin
```

To restore H2 database from specified backup

```
$H2_HOME/bin/h2-restore.sh jdbc:h2:tcp://localhost/test sa sa test.zip
```

H2 database interactive command line tool
-----------------------------------------

Copy [h2-shell.sh](https://github.com/evgeniy-khist/examples/blob/master/h2-server-init-script/h2-shell.sh) to _/opt/h2/bin_

```
wget --no-check-certificate https://raw.githubusercontent.com/evgeniy-khist/examples/master/h2-server-init-script/h2-shell.sh
cp h2-shell.sh $H2_HOME/bin
```

To start H2 database interactive command line tool

```
$H2_HOME/bin/h2-shell.sh jdbc:h2:tcp://localhost/test sa sa
```

[Original article](http://developer-should-know.tumblr.com/post/116316649672/how-to-install-h2-database-as-a-service-on-linux)
