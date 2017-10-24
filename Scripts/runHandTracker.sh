#!/bin/bash

source ~/mbv03.sh 
 
HANDTRACKERLIBS_DIR="/home/kyriazis/Documents/Programming/FORTH/HandTrackerRAPID/Resources/libs"

cd /home/kyriazis/Documents/Programming/FORTH/HandTrackerRAPID/target
java -Djava.library.path=$HANDTRACKERLIBS_DIR  -jar HandTrackerApp-0.0.4-SNAPSHOT.jar   &> ~/lastHandTrackerRun.txt 


exit 0
