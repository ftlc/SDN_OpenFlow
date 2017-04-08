#!/bin/sh

HOST=10.45.7.3
HOSTNAME=host3

#Perform shared updates
sh ./shared.sh $HOST

scp ./host3/interfaces root@$HOST:/etc/network/interfaces

ssh root@$HOST "/etc/init.d/networking restart"

#Setup Web Server
sh ./misc/setup_server.sh $HOST $HOSTNAME

