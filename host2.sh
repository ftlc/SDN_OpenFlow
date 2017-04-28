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

scp ./misc/switchifconfig1.sh root@$HOST:/home/cs4516/switchifconfig.sh

ssh root@$HOST "chmod +x /home/cs4516/switchifconfig.sh"
ssh root@$HOST "./home/cs4516/switchifconfig.sh"
scp ./misc/ifconfig.service root@$HOST:/etc/systemd/system/ifconfig.service

ssh root@$HOST "chmod 664 /etc/systemd/system/ifconfig.service"
ssh root@$HOST "systemctl enable ifconfig.service"


#Setup Web Server
sh ./misc/setup_server.sh $HOST $HOSTNAME
