#!/bin/sh

HOST=10.45.7.1

scp ./misc/sshd_config cs4516@$HOST:~/
ssh -t cs4516@$HOST "sudo mv ./sshd_config /etc/ssh/sshd_config"

scp ./misc/nanorc root@$HOST:/etc/nanorc
scp ./misc/sources.list root@$HOST:/etc/apt/sources.list
ssh root@$HOST "apt-get update"


scp ./host1/interfaces root@$HOST:/etc/network/interfaces

ssh root@$HOST "/etc/init.d/networking restart"
