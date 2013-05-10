#!/bin/bash
 
REMOTE=5160195a5973cacccd0000bc@skimbo-froggies.rhcloud.com
REMOTE_APP="~/diy-0.1/repo/"
REMOTE_START="~/diy-0.1/repo/.openshift/action_hooks/start"
REMOTE_STOP="~/diy-0.1/repo/.openshift/action_hooks/stop"
LOCAL_PATH_PLAY="/home/manland/projets/play-2.1.1"
LOCAL_PATH_PROJECT="/home/manland/projets/skimbo/Skimbo"

cd $LOCAL_PATH_PROJECT
bash -c "${LOCAL_PATH_PLAY}/play clean compile stage"
ssh $REMOTE "${REMOTE_STOP}";
rsync -va target/ $REMOTE:$REMOTE_APP/target
ssh $REMOTE "${REMOTE_START}";