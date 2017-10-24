#!/bin/bash

source ~/mbv03.sh 
 
HANDTRACKERLIBS_DIR="~/Documents/Programming/FORTH/rapid/HandTrackerRAPID/Resources/libs"

cd ~/Documents/Programming/FORTH/rapid/HandTrackerRAPID/target
java -Djava.library.path=$HANDTRACKERLIBS_DIR  -jar HandTrackerApp-0.0.4-SNAPSHOT.jar   &> ~/lastHandTrackerRun.txt 


exit 0
