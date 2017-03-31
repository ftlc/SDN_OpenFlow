#!/bin/sh

HOST=10.45.7.2


scp ./misc/nanorc root@$HOST:/etc/nanorc
scp ./misc/sources.list root@$HOST:/etc/apt/sources.list
ssh root@$HOST "apt-get update"


scp ./host2/interfaces root@$HOST:/etc/network/interfaces

ssh root@$HOST "/etc/init.d/networking restart"


ssh root@$HOST "apt-get install openvswitch-common openvswitch-switch
bridge-utils"
