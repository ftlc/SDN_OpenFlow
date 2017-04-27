#!/bin/sh

HOST=10.45.7.3
HOSTNAME=host3

#Perform shared updates
sh ./shared.sh $HOST host3

scp ./$HOSTNAME/aliases.sh root@$HOST:/home/cs4516/
ssh root@$HOST "chmod +x /home/cs4516/aliases.sh"
ssh root@$HOST "/home/cs4516/aliases.sh"
sh ./host3/aliases.sh

sh ./misc/setup_switch.sh $HOST

#Setup Web Server
sh ./misc/setup_server.sh $HOST $HOSTNAME


