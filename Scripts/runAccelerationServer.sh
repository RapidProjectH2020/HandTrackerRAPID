#!/bin/bash


DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

cd ~
HOME_DIR=`pwd`
cd "$DIR"

#source ~/mbv03.sh 
LIBS_DIR="$HOME_DIR/rapid-server/libs/"
 

cd ../../rapid-linux/AccelerationServer/target/
LD_LIBRARY_PATH=$LIBS_DIR java -Djava.library.path="$LIBS_DIR" -jar rapid-linux-as-0.0.2-SNAPSHOT.jar $@ &> ~/Documents/Programming/FORTH/rapid/lastAccelerationServerRun.txt 

exit 0
