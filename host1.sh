#!/bin/sh

HOST=10.45.7.1

#Perform shared setup
sh ./shared.sh $HOST host1

#scp ./host1/interfaces root@$HOST:/etc/network/interfaces

ssh root@$HOST "/etc/init.d/networking restart"


#Setup OpenvSwitch
sh ./misc/setup_switch.sh $HOST
