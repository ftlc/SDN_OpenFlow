#!/bin/sh

HOST=10.45.7.2
HOSTNAME=host2

#Perform shared updates
sh ./shared.sh $HOST host2

#Move over the interfaces file and restart networking
#scp ./host2/interfaces root@$HOST:/etc/network/interfaces
#ssh root@$HOST "/etc/init.d/networking restart"

#Run script to setup DNS server on host2
sh ./misc/setup_dns.sh $HOST

#Setup OpenVSwitch
sh ./misc/setup_switch.sh $HOST

#Setup Web Server
sh ./misc/setup_server.sh $HOST $HOSTNAME
