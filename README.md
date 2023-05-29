# Taxi-Server
## Overview
![alt text](https://github.com/phantruongbovi/Taxi-Server/blob/main/Diagram.png)
- To perform a test, I am currently storing 1 million vehicle records in Redis, evenly distributed among three types of vehicles (1, 2, 3).
## Setup
1. Clone repo
```
git clone https://github.com/phantruongbovi/Taxi-Server.git
```
2. Open repo
3. Config environment
- Access file **build.gradle** right click -> **link with gradle** -> **Gradle** -> **Task** -> **orther** -> **generateProto**
- Choose setting IDE -> **Build, Excution, Deployment** -> **Build Tools** -> **Gradle** 
-> Change **Build and run** from Gradle to IntelliJIDEA
## Run
1. Open src > main > java > taxi > client > Client.java -> RUN file
2. Ip and port:
- taxi-server: 20.43.157.85:50001
- osrm-server: 20.197.105.250:5000
3. Config VM: 
- vCPUs: 2
- RAM: 4 GiB
- Data disks: 4
- Max IOPS: 1280
- Temp storage: 8 GiB
