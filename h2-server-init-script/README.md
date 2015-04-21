H2 Server Init Script
=====================

Installing H2 database as a service
-----------------------------------

Installing H2 database as a service on Linux has multiple advantages like automatic start on system boot, convinient management with service command and other.

To install H2 database as a service on CentOS and other RPM-based Linux distributions do steps described below.

H2 database installation should have the following directory layout:

```
/opt/h2/
       |_ bin/
       |     |_ h2*.jar (H2 jar)
       |     |_ [h2-backup.sh](https://github.com/evgeniy-khist/examples/blob/master/h2-server-init-script/h2-backup.sh)
       |     |_ [h2-restore.sh](https://github.com/evgeniy-khist/examples/blob/master/h2-server-init-script/h2-restore.sh)
       |     |_ [h2-shell.sh](https://github.com/evgeniy-khist/examples/blob/master/h2-server-init-script/h2-shell.sh)
       |     |_ h2.pid
       |_ log/
       |     |_ h2.log
       |_ db/
            |_ example.h2.db (database file)
            |_ example.20150413104651.zip (backup file)
```

Create directories _bin_, _log_ and _db_ under _/opt/h2/_.

Copy [h2-init-redhat.sh](https://github.com/evgeniy-khist/examples/blob/master/h2-server-init-script/h2-init-redhat.sh) to _/etc/init.d/h2_

```
cp h2-init-redhat.sh /etc/init.d/h2
```

Copy [h2.conf](https://github.com/evgeniy-khist/examples/blob/master/h2-server-init-script/h2.conf) to _/etc/default/h2.conf_

```
cp h2.conf /etc/default/h2.conf
```

Make _/etc/init.d/h2_ executable

```
chmod +x /etc/init.d/h2
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

Backing up H2 database
----------------------

Copy [h2-backup.sh](https://github.com/evgeniy-khist/examples/blob/master/h2-server-init-script/h2-backup.sh) to _$H2_HOME/bin_

```
cp h2-backup.sh $H2_HOME/bin
```

To backup H2 database

```
$H2_HOME/bin/h2-backup.sh jdbc:h2:tcp://localhost/test sa sa test.zip
```

Backup script will be located at `$H2_HOME/bin/test.zip`.

Restoring H2 database
---------------------

Copy [h2-restore.sh](https://github.com/evgeniy-khist/examples/blob/master/h2-server-init-script/h2-restore.sh) to _$H2_HOME/bin_

```
cp h2-restore.sh $H2_HOME/bin
```

To restore H2 database from specified backup

```
$H2_HOME/bin/h2-restore.sh jdbc:h2:tcp://localhost/test sa sa test.zip
```

H2 database interactive command line tool
-----------------------------------------

Copy [h2-shell.sh](https://github.com/evgeniy-khist/examples/blob/master/h2-server-init-script/h2-shell.sh) to _$H2_HOME/bin_

```
cp h2-shell.sh $H2_HOME/bin
```

To start H2 database interactive command line tool

```
$H2_HOME/bin/h2-shell.sh jdbc:h2:tcp://localhost/test sa sa
```

[Original article](http://developer-should-know.tumblr.com/post/116316649672/how-to-install-h2-database-as-a-service-on-linux)
