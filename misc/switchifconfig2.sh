#!/bin/sh

service openvswitch-switch start
ovs-vsctl add-br br0 && ovs-vsctl add-port br0 eth0 && \
	ifconfig eth0 0 && dhclient -r eth0 &&  dhclient br0 && ifconfig br0 \
	10.45.7.2 && ovs-vsctl set-controller br0 tcp:10.45.7.4:6653
##shutdown -h now
