#!/bin/sh
URL=$1
FILE=$URL.csv
printf "\"namelookup\",\"connect\",\"appconnect\",\"pretransfer\",\"redirect\",\"starttransfer\",\"total\"\n" >$FILE
for i in {1..100}; do
	curl -w "@curl-format.txt" -o /dev/null -s $URL >>$FILE
	echo $FILE $i
done
