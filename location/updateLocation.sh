#!/bin/bash
ts=$(date +%s%N)
cat /home/dipper/Documents/taxiWithRedis/location/main/LocationType1.txt | redis-cli -h 20.43.169.247 -p 6379 --pipe
cat /home/dipper/Documents/taxiWithRedis/location/main/LocationType2.txt | redis-cli -h 20.43.170.85 -p 6379 --pipe
cat /home/dipper/Documents/taxiWithRedis/location/main/LocationType3.txt | redis-cli -h 20.198.152.132 -p 6379 --pipe
echo "Time: $((($(date +%s%N) - $ts)/1000000)) ms" >> file_to_append_time_to
