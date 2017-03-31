#!/bin/sh

HOST=10.45.7.4


scp ./misc/nanorc root@$HOST:/etc/nanorc
scp ./misc/sources.list root@$HOST:/etc/apt/sources.list
ssh root@$HOST "apt-get update"


scp ./host4/interfaces root@$HOST:/etc/network/interfaces

ssh root@$HOST "/etc/init.d/networking restart"


ssh root@$HOST "apt-get install lighttpd floodlight bind9 bind9utils bind9-doc"
scp ./misc/lighttpd.conf root@$HOST:/etc/lighttpd/lighttpd.conf
ssh root@$HOST "/etc/init.d/lighttpd start"
ssh root@$HOST "mkdir /var/www/"
ssh root@$HOST "mkdir /var/www/html/"
scp ./host4/index.lighttpd.html root@$HOST:/var/www/html/index.lighttpd.html



ssh root@$HOST "mkdir /etc/bind/zones/"


scp ./host4/db.team7.4516.cs.wpi.edu root@$HOST:/etc/bind/zones/db.team7.4516.cs.wpi.edu
scp ./host4/named.conf.local root@$HOST:/etc/bind/named.conf.local
scp ./host4/named.conf.options root@$HOST:/etc/bind/named.conf.options
scp ./host4/named.conf root@$HOST:/etc/bind/named.conf


ssh root@$HOST "named-checkconf"
ssh root@$HOST "named-checkzone team7.4516.cs.wpi.edu /etc/bind/zones/db.team7.4516.cs.wpi.edu"
ssh root@$HOST "systemctl stop bind9"
ssh root@$HOST "systemctl start bind9"
