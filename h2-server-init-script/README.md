H2 Server Init Script
=====================

Installing H2 database as a service on Linux has multiple advantages like automatic start on system boot, convinient management with service command and other.

To install H2 database as a service on CentOS and other RPM-based Linux distributions do steps described below.

H2 database installation should have the following directory layout:

```
/opt/h2/
       |_ bin/
       |     |_ h2*.jar (H2 jar)
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
copy h2-init-redhat.sh /etc/init.d/h2
```

Copy [h2.conf](https://github.com/evgeniy-khist/examples/blob/master/h2-server-init-script/h2.conf) to _/etc/default/h2.conf_

```
copy h2.conf /etc/default/h2.conf
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

To backup H2 database

```
service h2 backup
```

Backup script will be located in _/opt/h2/db/$DATABASE.$DATE.zip_.

To restore H2 database from specified backup

```
service h2 restore example.20150413104651.zip
```

[Original article](http://developer-should-know.tumblr.com/post/116316649672/how-to-install-h2-database-as-a-service-on-linux)
