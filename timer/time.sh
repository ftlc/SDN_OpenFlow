#!/bin/bash
URL=$1
FILE=$URL.csv
printf "\"namelookup\",\"connect\",\"appconnect\",\"pretransfer\",\"redirect\",\"starttransfer\",\"total\",\"result\"\n" >$FILE
for i in {1..100}; do
	curl -w "@curl-format.txt" -s $URL -o tmpresult >>$FILE
	declare -i RESULT ##force to int
	RESULT=$(cat tmpresult | grep 'RESULT' | cut -d ' ' -f 2)
	printf " %i\n" "$RESULT"  >> $FILE
	rm tmpresult
	printf "%s %i %i\n" "$FILE" "$i" "$RESULT"
done
