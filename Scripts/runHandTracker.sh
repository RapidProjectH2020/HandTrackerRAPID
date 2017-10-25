#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

source ~/mbv03.sh 
 
HANDTRACKERLIBS_DIR="$DIR/../Resources/libs"
#HANDTRACKERLIBS_DIR="/home/ammar/rapid-server/libs/"

cd $DIR/../target
LD_LIBRARY_PATH=$HANDTRACKERLIBS_DIR java -Djava.library.path=$HANDTRACKERLIBS_DIR  -jar HandTrackerApp-0.0.4-SNAPSHOT.jar   &> ~/Documents/Programming/FORTH/rapid/lastHandTrackerRun.txt 


exit 0
