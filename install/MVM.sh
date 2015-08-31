#!/bin/bash
MVM_HOME="/usr/local/mvm"
MVM_Exec="$MVM_HOME"/mvm.jar
java -jar $MVM_Exec "$@" &
