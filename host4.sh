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
ssh root@$HOST "apt-get remove python"
ssh root@$HOST "apt-get install python"
ssh root@$HOST "apt-get install -f python"
ssh root@$HOST "apt-get remove python-minimal"
ssh root@$HOST "apt-get remove libpython-stdlib"
ssh root@$HOST "apt-get install python"
ssh root@$HOST "apt-get install python-dev"
ssh root@$HOST "apt-get remove libpython-dev python2.7-dev"
ssh root@$HOST "apt-get install libpython-dev" 
ssh root@$HOST "apt-get install libpython2.7-dev"
ssh root@$HOST "apt-get install libpython2.7"
ssh root@$HOST "apt-get remove libpython2.7 libpython2.7-stdlib"
ssh root@$HOST "apt-get install libpython2.7"
ssh root@$HOST "apt-get install libpython2/7-stdlib"
ssh root@$HOST "apt-get remove libpython2.7-minimal"
ssh root@$HOST "apt-get install libpython2.7-minimal"
ssh root@$HOST "apt-get install libpython2.7-stdlib"
ssh root@$HOST "apt-get install libpython2.7"
ssh root@$HOST "apt-get install libpython-dev"
ssh root@$HOST "apt-get install libpython2.7-dev" 
ssh root@$HOST "apt-get install libexpat1-dev"
ssh root@$HOST "apt-get install libexpat1"
ssh root@$HOST "apt-get remove libexpat1"
ssh root@$HOST "apt-get update libexpat" 
ssh root@$HOST "apt-get install libexpat1" 
ssh root@$HOST "apt-get install libexpat1-dev" 
ssh root@$HOST "apt-get install libexpat1=2.1.0-4ubuntu1.3"
ssh root@$HOST "apt-get install libexpat1-dev"
ssh root@$HOST "apt-get install python-dev" 
