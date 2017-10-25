#!/bin/bash

source ~/mbv03.sh 
 
HANDTRACKERLIBS_DIR="/home/ammar/Documents/Programming/FORTH/rapid/HandTrackerRAPID/Resources/libs"
HANDTRACKERLIBS_DIR="/home/ammar/rapid-server/libs/"

cd ~/Documents/Programming/FORTH/rapid/HandTrackerRAPID/target
LD_LIBRARY_PATH=$HANDTRACKERLIBS_DIR java -Djava.library.path=$HANDTRACKERLIBS_DIR  -jar HandTrackerApp-0.0.4-SNAPSHOT.jar   &> ~/Documents/Programming/FORTH/rapid/lastHandTrackerRun.txt 


exit 0
