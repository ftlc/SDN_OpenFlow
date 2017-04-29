#!/bin/bash

HOST=10.45.7.1
HOSTNAME=host1

#Perform shared setup
bash ./shared.sh $HOST host1

#scp ./host1/interfaces root@$HOST:/etc/network/interfaces
#ssh root@$HOST "/etc/init.d/networking restart"

scp ./$HOSTNAME/aliases.sh root@$HOST:/home/cs4516/
ssh root@$HOST "chmod +x /home/cs4516/aliases.sh"
ssh root@$HOST "/home/cs4516/aliases.sh"


#Setup OpenvSwitch
sh ./misc/setup_switch.sh $HOST
