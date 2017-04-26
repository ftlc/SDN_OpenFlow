NIC="eth0"
MAC=$(ifconfig $NIC | grep "HWaddr\b" | awk '{print $5}')  
ovs-vsctl add-br br0 -- set bridge br0 other-config:hwaddr=$MAC
ovs-vsctl add-port br0 $NIC > /dev/null 2>&1
ifconfig eth0 0 
dhclient -r eth0 
dhclient br0 
ifconfig br0 $HOST
ifconfig $NIC 0.0.0.0
LAST_MAC_CHAR=${MAC:(-1)}
AUX="${MAC:0:${#MAC}-1}"
if [ "$LAST_MAC_CHAR" -eq "$LAST_MAC_CHAR" ] 2>/dev/null; then
    NL="a"
else
    NL="1"
fi
NEW_MAC="$AUX$NL"
ifconfig $NIC hw ether $NEW_MAC
ovs-vsctl set-controller br0 tcp:10.45.7.4:6653
