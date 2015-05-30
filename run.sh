#!/bin/sh

DIR=`pwd`
cd ${DIR}/game
rm -rf works
git clone https://github.com/nju525/works.git
cd works/target/classes
rm -rf huawei
cd /home/game/game/works/makeproject
make
cd ${DIR}/game
sh dist_check_and_run.sh

ps | grep gameserver

while [ $? -eq 0 ]
do
echo "game is running"
ps | grep gameserver
done


cd /home/game/game/works
git add source
git commit -m "v1.0"
git push

cd /home/game/game
rm -rf works/.git
tar cvf works.tar.gz works/

cd /home/game
rm -rf dzpk
git clone https://github.com/nju525/dzpk.git
mkdir -p /home/game/dzpk/log
cp /home/game/run_area/server/replay.txt /home/game/dzpk/log
cp /home/game/run_area/server/log.txt /home/game/dzpk/log
cp /home/game/run_area/server/data.csv /home/game/dzpk/log
cp /home/game/game/works.tar.gz /home/game/dzpk
cd /home/game/dzpk

git add log
git add works.tar.gz
git commit -m "v1.0"
git push



