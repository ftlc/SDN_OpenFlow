#!/bin/bash
URL=$1
FILE=$URL.csv
printf "\"connect\",\"appconnect\",\"pretransfer\",\"redirect\",\"starttransfer\",\"total\",\"result\"\n" >$FILE
for i in {1..100}; do
	if [[ -z "$HOST" ]]; then
		HOST=$(dig +short $URL | head -n1)
		echo got host $HOST
	fi
	curl -w "@curl-format.txt" -s $HOST -o tmpresult >>$FILE
	if [ $? -ne 0 ]; then
		HOST=
		echo failed
	fi
	declare -i RESULT ##force to int
	RESULT=$(cat tmpresult | grep 'RESULT' | cut -d ' ' -f 2)
	printf " %i\n" "$RESULT"  >> $FILE
	rm tmpresult
	printf "%s %i %i\n" "$FILE" "$i" "$RESULT"
done
