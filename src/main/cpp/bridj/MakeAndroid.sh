#!/bin/bash

function fail {
    echo "#" >&2
    echo "# ERROR: $@" >&2
    echo "#" >&2
    exit 1
}

cd $(dirname $0)

BRIDJ_HOME="$PWD/../../../.."
RESOURCES="$BRIDJ_HOME/src/main/resources"
DYNCALL_HOME="$BRIDJ_HOME/dyncall"
NDK_PROJECT_PATH="$PWD"

[[ -n "$ANDROID_NDK_HOME" ]] || fail "ANDROID_NDK_HOME not defined."

if [[ ! -e jni/dyncall ]]; then
    ln -s "$DYNCALL_HOME/dyncall/" jni/dyncall
fi

for ABI in x86 armeabi; do
    $ANDROID_NDK_HOME/ndk-build "APP_ABI=$ABI" $@ || fail "Failed to build Android lib for ABI $ABI"
    if [[ "$@" != "clean" ]]; then
        LIB_DIR="$RESOURCES/libs/$ABI"
        [ -d "$LIB_DIR" ] || mkdir -p "$LIB_DIR" || fail "Failed to create output dir $LIB_DIR"
        cp libs/$ABI/*.so "$LIB_DIR"
    fi
done

rm jni/dyncall || fail "Failed to remove jni/dyncall symlink"
