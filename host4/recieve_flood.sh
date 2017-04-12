#!/bin/sh

HOST=$1
HOSTNAME=$2


cd host4
scp cs4516@$HOST:~/floodlight/src/main/java/net/floodlightcontroller/cs4516/CS4516.java ./CS4516.java
