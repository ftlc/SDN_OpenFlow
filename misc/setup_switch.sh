#!/bin/sh

HOST=$1

ssh root@$HOST "apt-get install openvswitch-common openvswitch-switch bridge-utils"
ssh root@$HOST "sysctl -w net.ipv4.ip_forward=0"
ssh root@$HOST "service openvswitch-switch start"
scp test.sh cs4516@$HOST:~/switch.sh
ssh root@$HOST "chmod +x ~/switch.sh"
ssh root@$HOST "sh ~/switch.sh"
#ssh root@$HOST "ovs-vsctl add-br br0 && ovs-vsctl add-port br0 eth0 && \
#	ifconfig eth0 0 && dhclient -r eth0 &&  dhclient br0 && ifconfig br0 \
#	$HOST && ovs-vsctl set-controller br0 tcp:10.45.7.4:6653"


