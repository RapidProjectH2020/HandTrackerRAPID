#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

cd ..
cd ..
RAPID_DIR=`pwd`

cd ~
HOME_DIR=`pwd`
cd "$DIR"


notify-send "Starting Build procedure"

cd $RAPID_DIR/rapid-common
mvn clean
mvn install

cd $RAPID_DIR/rapid-linux/rapid-gvirtus4j
mvn clean
mvn install

cd $RAPID_DIR/AccelerationClient
mvn clean
mvn install
cd target 
ln -s $RAPID_DIR/HandTrackerRAPID/Resources/media
cd ..

cd $RAPID_DIR/rapid-linux/AccelerationServer
mvn clean
mvn install
cd target 
ln -s $RAPID_DIR/HandTrackerRAPID/Resources/media
cd ..
notify-send "Build Acceleration Server"

#cd /home/kyriazis/projects/offloading-framework-linux/DemoApp
cd $RAPID_DIR/rapid-linux-DemoApp
mvn clean
mvn package

#cd /home/kyriazis/projects/mbv/build
#make
#make install

#cd /home/kyriazis/projects/HandTrackerRGBD/build
#make
#make install

#cd /home/kyriazis/projects/HandTrackerJNI/build
#make
#make install
#notify-send "Build Hand Tracker JNI Wrapper"

#cd /home/kyriazis/projects/HandTrackerRAPID
cd $RAPID_DIR/HandTrackerRAPID
mvn clean
mvn package
cd target
ln -s $RAPID_DIR/HandTrackerRAPID/Resources/media
notify-send "Build Hand Tracker Application"

echo "DONE WITH EVERYTHING"
notify-send "DONE WITH EVERYTHING"

exit 0

