#!/bin/bash
function testCMD() 
{
	echo "$@"
    "$@"
    local status=$?
    if [ $status -ne 0 ]; then
        echo "ERROR: code: $status"
        exit 255
    fi
    return $status
}

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
cd "$DIR"
MVM_HOME="/usr/local/mvm"
testCMD mvn package
echo "mkdir \"$MVM_HOME\""
mkdir "$MVM_HOME"
testCMD cp "./target/mvm.jar" "$MVM_HOME/mvm.jar"
testCMD cp "./install/MVM.sh" "$MVM_HOME/MVM.sh"
testCMD chmod +x "$MVM_HOME/MVM.sh"
testCMD sudo ln -sf "$MVM_HOME/MVM.sh" "/usr/local/bin/mvm"
echo checking
testCMD which mvm
testCMD mvm -version
