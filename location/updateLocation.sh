#!/bin/bash
ts=$(date +%s%N)
cat /home/dipper/Documents/taxiWithRedis/location/main/LocationType1.txt | redis-cli -h 20.184.57.114 -p 6379 --pipe
cat /home/dipper/Documents/taxiWithRedis/location/main/LocationType2.txt | redis-cli -h 20.184.59.153 -p 6379 --pipe
cat /home/dipper/Documents/taxiWithRedis/location/main/LocationType3.txt | redis-cli -h 20.184.59.186 -p 6379 --pipe
echo "formatting $((($(date +%s%N) - $ts)/1000000)) formatting" >> file_to_append_time_to
