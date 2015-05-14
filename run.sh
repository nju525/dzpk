#!/bin/sh

DIR=`pwd`
cd ${DIR}/game
rm -rf works
git clone https://github.com/nju525/dzpk.git
mv dzpk works
cd works/makeproject
make
cd ${DIR}/game
sh dist_check_and_run.sh


#echo ${DIR}
#cd ${DIR}
#ls
#mkdir -p ${DIR}/game/works/log
#ls /root/run_area/server/replay.txt
#cp /root/run_area/server/replay.txt ${DIR}/game/works/log
#ls /root/run_area/server/
#cp /root/run_area/server/log.txt ${DIR}/game/works/log
#cd ${DIR}/game/works
#git add ./log
#git commit -m "test log"
#git push
#'''
