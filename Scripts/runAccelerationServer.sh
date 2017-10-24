#!/bin/bash

#source ~/mbv03.sh 
HANDTRACKERLIBS_DIR="/home/ammar/Documents/Programming/FORTH/rapid/HandTrackerRAPID/Resources/libs/"
HANDTRACKERLIBS_DIR="/home/ammar/rapid-server/libs/"
ACCEL_DIR="/home/ammar/Documents/Programming/FORTH/rapid/rapid-linux/AccelerationServer/target/"

cd $ACCEL_DIR
LD_LIBRARY_PATH=$HANDTRACKERLIBS_DIR java -Djava.library.path=$HANDTRACKERLIBS_DIR -jar rapid-linux-as-0.0.2-SNAPSHOT.jar &> /home/ammar/Documents/Programming/FORTH/rapid/lastAccelerationServerRun.txt 

exit 0
