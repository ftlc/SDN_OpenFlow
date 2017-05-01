#!/bin/bash

HOST=10.45.7.2
HOSTNAME=host2

#Perform shared updates
bash ./shared.sh $HOST host2

#Move over the interfaces file and restart networking
#scp ./host2/interfaces root@$HOST:/etc/network/interfaces
#ssh root@$HOST "/etc/init.d/networking restart"

#Run script to setup DNS server on host2
sh ./misc/setup_dns.sh $HOST $HOSTNAME

#Setup OpenVSwitch
sh ./misc/setup_switch.sh $HOST

scp ./misc/switchifconfig2.sh root@$HOST:/home/cs4516/switchifconfig.sh

ssh root@$HOST "chmod +x /home/cs4516/switchifconfig.sh"
ssh root@$HOST "/home/cs4516/switchifconfig.sh"
#scp ./misc/ifconfig.service root@$HOST:/etc/systemd/system/fuckfig.service

#ssh root@$HOST "chmod 664 /etc/systemd/system/fuckfig.service"
#ssh root@$HOST "systemctl daemon-reload"
#ssh root@$HOST "systemctl enable fuckfig.service"


#Setup Web Server
sh ./misc/setup_server.sh $HOST $HOSTNAME
