#!/bin/sh

HOST=$1

ssh root@$HOST "apt-get install openvswitch-switch bridge-utils"
ssh root@$HOST "service openvswitch-switch start"
ssh root@$HOST "ovs-vsctl add-br br0 && ovs-vsctl add-port br0 eth0 && \
	ifconfig eth0 0 && dhclient -r eth0 &&  dhclient br0 && ifconfig br0 \
	$HOST && ovs-vsctl set-controller br0 tcp:10.45.7.4:6653"
ssh root@$HOST "systemctl enable openvswitch-switch"
