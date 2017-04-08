#!/bin/sh

HOST=$1
HOSTNAME=$2


ssh root@$HOST "apt-get install bind9 bind9utils bind9-doc"
ssh root@$HOST "mkdir /etc/bind/zones/"

scp ../$HOSTNAME/dns/db.team7.4516.cs.wpi.edu root@$HOST:/etc/bind/zones/db.team7.4516.cs.wpi.edu
scp ../$HOSTNAME/dns/named.conf.local root@$HOST:/etc/bind/named.conf.local
scp ../$HOSTNAME/dns/named.conf.options root@$HOST:/etc/bind/named.conf.options
scp ../$HOSTNAME/dns/named.conf root@$HOST:/etc/bind/named.conf

ssh root@$HOST "named-checkconf"
ssh root@$HOST "named-checkzone team7.4516.cs.wpi.edu /etc/bind/zones/db.team7.4516.cs.wpi.edu"
ssh root@$HOST "systemctl stop bind9"
ssh root@$HOST "systemctl start bind9"
