#!/bin/bash


H=$1
HN=$2

echo host $H name $HN
scp ./misc/sshd_config cs4516@$H:~/
ssh -t cs4516@$H "sudo mv ./sshd_config /etc/ssh/sshd_config"

echo big

if [[ -z "$HN" ]]; then
	echo "You forgot a hostname!"
else
##	ssh root@$H "hostname $HN"
##	ssh root@$H "echo $HN > /etc/hostname"
##	scp $HN/hosts root@$H:/etc/hosts
	echo fuuuug
fi
scp ./misc/tmux.conf root@$H:/etc/tmux.conf
scp ./misc/nanorc root@$H:/etc/nanorc
scp ./misc/sources.list root@$H:/etc/apt/sources.list
ssh root@$H "apt-get update && apt-get upgrade"
