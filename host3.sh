#!/bin/sh

HOST=10.45.7.3


scp ./misc/nanorc root@$HOST:/etc/nanorc
scp ./misc/sources.list root@$HOST:/etc/apt/sources.list
ssh root@$HOST "apt-get update"


scp ./host3/interfaces root@$HOST:/etc/network/interfaces

ssh root@$HOST "/etc/init.d/networking restart"


ssh root@$HOST "apt-get install lighttpd"
scp ./misc/lighttpd.conf root@$HOST:/etc/lighttpd/lighttpd.conf
ssh root@$HOST "/etc/init.d/lighttpd start"
ssh root@$HOST "mkdir /var/www/"
ssh root@$HOST "mkdir /var/www/html/"
scp ./host3/index.lighttpd.html root@$HOST:/var/www/html/index.lighttpd.html
