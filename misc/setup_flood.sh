#!/bin/sh

HOST=$1
HOSTNAME=$2

#Setup floodlight
scp misc/floodlight.zipa* cs4516@$HOST:~/
ssh cs4516@$HOST "cat ./floodlight.zipa* > ./floodlight.zip && unzip floodlight.zip"
scp misc/CS4516.java cs4516@$HOST:~/floodlight/src/main/java/net/floodlightcontroller/cs4516/CS4516.java

