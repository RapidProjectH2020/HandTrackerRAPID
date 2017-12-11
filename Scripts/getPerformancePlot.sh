#!/bin/bash


function getStatistics 
{ #use R to generate statistics
  R -q -e "x <- read.csv('$1', header = F); summary(x); sd(x[ , 1])" > $2
  cat $1 | wc -l >> $2 
}  


DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

cd ..
cd ..
cat lastHandTrackerRun.txt | grep FPS | cut -d ' ' -f2,3 > performance

GNUPLOT_CMD="set terminal png; set output \"performance.png\"; set yrange [0:25];  set ylabel \"Framerate FPS\"; \
plot \"performance\" using 1:2  with lines smooth bezier title \"Performance Graph\" "

gnuplot -e "$GNUPLOT_CMD"


exit 0
