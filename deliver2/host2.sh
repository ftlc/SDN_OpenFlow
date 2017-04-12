#!/bin/sh

HOST=10.45.7.2

scp ./misc/sshd_config cs4516@$HOST:~/
ssh -t cs4516@$HOST "sudo mv ./sshd_config /etc/ssh/sshd_config"

scp ./misc/nanorc root@$HOST:/etc/nanorc
scp ./misc/sources.list root@$HOST:/etc/apt/sources.list
ssh root@$HOST "apt-get update"


scp ./host2/interfaces root@$HOST:/etc/network/interfaces

ssh root@$HOST "/etc/init.d/networking restart"


ssh root@$HOST "apt-get install openvswitch-common openvswitch-switch bridge-utils"

ssh root@$HOST "sysctl -w net.ipv4.ip_forward=0"

ssh root@$HOST "service openvswitch-switch start"

ssh root@$HOST "ovs-vsctl add-br br0 && ovs-vsctl add-port br0 eth0 && \
	ifconfig eth0 0 && dhclient -r eth0 &&  dhclient br0 && ifconfig br0 \
	10.45.7.2 && ovs-vsctl set-controller br0 tcp:10.45.7.4:6653"

ssh root@$HOST "apt-get install lighttpd"
scp ./misc/lighttpd.conf root@$HOST:/etc/lighttpd/lighttpd.conf
ssh root@$HOST "/etc/init.d/lighttpd start"
ssh root@$HOST "mkdir /var/www/"
ssh root@$HOST "mkdir /var/www/html/"
scp ./host2/index.lighttpd.html root@$HOST:/var/www/html/index.lighttpd.html
