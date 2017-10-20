#!/bin/bash

#This is a script to package a snapshot of all .so 
#libraries in the libs/ directory 
#https://github.com/AmmarkoV/Unix-Commandline-Apps/blob/master/lddr/main.c

 
green=$(printf "\033[32m") 
normal=$(printf "\033[m")


THEDATETAG=`date +"%y-%m-%d_%H-%M-%S"`

OURDISTRO=`lsb_release -a | grep "Distributor ID" | cut  -f2`
OURVER=`lsb_release -a | grep Release | cut  -f2`
echo "Our Distro is $OURDISTRO $OURVER"


NAME="HandTrackerRAPIDLibs$OURDISTRO$OURVER-$THEDATETAG"
 

tar cvfjh "$NAME.tar.bz2" libs/
 

echo "$green Done.. $normal"
exit 0
