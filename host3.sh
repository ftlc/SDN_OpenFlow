#!/bin/bash

HOST=10.45.7.3
HOSTNAME=host3

#Perform shared updates
bash ./shared.sh $HOST host3

scp ./$HOSTNAME/aliases.sh root@$HOST:/home/cs4516/
ssh root@$HOST "chmod +x /home/cs4516/aliases.sh"
ssh root@$HOST "/home/cs4516/aliases.sh"
##sh ./host3/aliases.sh

sh ./misc/setup_switch.sh $HOST

#Setup OpenVSwitch
sh ./misc/setup_switch.sh $HOST

scp ./misc/switchifconfig3.sh root@$HOST:/home/cs4516/switchifconfig.sh

ssh root@$HOST "chmod +x /home/cs4516/switchifconfig.sh"
ssh root@$HOST "/home/cs4516/switchifconfig.sh"
scp ./misc/ifconfig.service root@$HOST:/etc/systemd/system/fuckfig.service

ssh root@$HOST "chmod 664 /etc/systemd/system/fuckfig.service"
ssh root@$HOST "systemctl daemon-reload"
ssh root@$HOST "systemctl enable fuckfig.service"
