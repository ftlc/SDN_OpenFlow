#!/bin/sh

HOST=$1
HOSTNAME=$2

#Setup Web Server
ssh root@$HOST "apt-get install lighttpd"
scp ./lighttpd.conf root@$HOST:/etc/lighttpd/lighttpd.conf
ssh root@$HOST "/etc/init.d/lighttpd start"
ssh root@$HOST "mkdir /var/www/"
ssh root@$HOST "mkdir /var/www/html/"
scp ../$HOSTNAME/index.lighttpd.html root@$HOST:/var/www/html/index.lighttpd.html
