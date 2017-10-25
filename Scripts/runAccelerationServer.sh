#!/bin/bash

#source ~/mbv03.sh 
HANDTRACKERLIBS_DIR="/home/ammar/Documents/Programming/FORTH/rapid/HandTrackerRAPID/Resources/libs/"
HANDTRACKERLIBS_DIR="/home/ammar/rapid-server/libs/"
 

cd ~/Documents/Programming/FORTH/rapid/rapid-linux/AccelerationServer/target/
LD_LIBRARY_PATH=$HANDTRACKERLIBS_DIR java -Djava.library.path=$HANDTRACKERLIBS_DIR -jar rapid-linux-as-0.0.2-SNAPSHOT.jar &> ~/Documents/Programming/FORTH/rapid/lastAccelerationServerRun.txt 

exit 0
