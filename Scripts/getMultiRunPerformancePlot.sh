#!/bin/bash


function getStatistics 
{ #use R to generate statistics
  R -q -e "x <- read.csv('$1', header = F); summary(x); sd(x[ , 1])" > $2
  cat $1 | wc -l >> $2 
}  


DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"
 
cat desktopLocal/lastHandTrackerRun.txt | grep FPS | cut -d ' ' -f2,3 > desktopLocalperformance
cat desktopLocalOneStep/performance | grep FPS | cut -d ' ' -f2,3 > desktopLocalOneStepPerformance
cat desktopBinary/binaryPerformance  | grep FPS | cut -d ' ' -f2,3 > desktopBinaryPerformance
cat laptopLocal/lastHandTrackerRun.txt | grep FPS | cut -d ' ' -f2,3 > laptopLocalperformance
cat laptopWifiToDesktop/lastHandTrackerRun.txt | grep FPS | cut -d ' ' -f2,3 > laptopWifiToDesktopperformance
cat laptopEthToDesktop/lastHandTrackerRun.txt | grep FPS | cut -d ' ' -f2,3 > laptopEthToDesktopperformance

GNUPLOT_CMD="set terminal png; set output \"performance.png\"; set yrange [0:40];  set ylabel \"Framerate FPS\"; \
plot \"desktopLocalperformance\" using 1:2  with lines smooth bezier title \"Desktop Local Run Performance Graph\",\
  \"desktopLocalOneStepPerformance\" using 1:2  with lines smooth bezier title \"Desktop One Step Local Run Performance Graph\",\
  \"laptopEthToDesktopperformance\" using 1:2  with lines smooth bezier title \"Laptop To Desktop Ethernet Performance Graph\",\
  \"desktopBinaryPerformance\" using 1:2  with lines smooth bezier title \"Desktop Binary Performance Graph\",\
  \"laptopLocalperformance\" using 1:2  with lines smooth bezier title \"Laptop Local Run Performance Graph\",\
  \"laptopWifiToDesktopperformance\" using 1:2  with lines smooth bezier title \"Laptop To Desktop Wifi Performance Graph\" "

gnuplot -e "$GNUPLOT_CMD"


exit 0
