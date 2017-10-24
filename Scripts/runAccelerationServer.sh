#!/bin/bash

#source ~/mbv03.sh 
HANDTRACKERLIBS_DIR="~/Documents/Programming/FORTH/rapid/HandTrackerRAPID/Resources/libs/"
HANDTRACKERLIBS_DIR="~/rapid-server/libs/"
ACCEL_DIR="~/Documents/Programming/FORTH/rapid/rapid-linux/AccelerationServer/target/"

cd $ACCEL_DIR
LD_LIBRARY_PATH=$HANDTRACKERLIBS_DIR java -Djava.library.path=$HANDTRACKERLIBS_DIR -jar rapid-linux-as-0.0.2-SNAPSHOT.jar &> ~/Documents/Programming/FORTH/rapid/lastAccelerationServerRun.txt 

exit 0
