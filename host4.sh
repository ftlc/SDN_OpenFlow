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
