#!/bin/bash

function fail {
    echo "#" >&2
    echo "# ERROR: $@" >&2
    echo "#" >&2
    exit 1
}

cd $(dirname $0)

BRIDJ_HOME=$PWD/../../../..
RESOURCES=$BRIDJ_HOME/src/main/resources
DYNCALL_HOME=$BRIDJ_HOME/dyncall
NDK_PROJECT_PATH=$PWD

if [[ ! -e jni/dyncall ]]; then
    ln -s $(DYNCALL_HOME)/dyncall/ jni/dyncall
elif [[Ê! -h jni/dyncall ]]; then
    fail "File jni/dyncall is meant to be a symbolic link to $(DYNCALL_HOME)/dyncall"
fi

for ABI in x86 armeabi; do
    ~/bin/android-ndk-r8d/ndk-build "APP_ABI=$ABI" $@ || fail "Failed to build for ABI $ABI"
    [[ "$@" != "clean" ]] && cp libs/$ABI/*.so $RESOURCES/libs/$ABI
done
