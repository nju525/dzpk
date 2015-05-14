#!/bin/sh

mkdir -p /root/game/works/log
ls /root/run_area/server/replay.txt
cp /root/run_area/server/replay.txt /root/game/works/log
ls /root/run_area/server/
cp /root/run_area/server/log.txt /root/game/works/log
cd /root/game/works
git add ./log
git commit -m "test log"
git push
