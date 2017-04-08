#!/bin/sh

HOST=10.45.7.4
HOSTNAME=host4

#Perform shared updates
sh ./shared.sh $HOST


scp ./$HOSTNAME/interfaces root@$HOST:/etc/network/interfaces

ssh root@$HOST "/etc/init.d/networking restart"



##Setup Server
#sh ./misc/setup_server.sh $HOST $HOSTNAME

##Setup DNS
#sh ./misc/setup_dns.sh $HOST $HOSTNAME


ssh root@$HOST "apt-get install python-dev ant maven build-essential"


