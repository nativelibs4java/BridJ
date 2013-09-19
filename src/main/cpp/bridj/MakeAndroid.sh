#!/bin/bash

function fail {
    echo "#" >&2
    echo "# ERROR: $@" >&2
    echo "#" >&2
    exit 1
}

cd $(dirname $0)

BRIDJ_HOME="$PWD/../../../.."
LIBS_DIR="$BRIDJ_HOME/src/main/android-libs"
DYNCALL_HOME="$BRIDJ_HOME/dyncall"
NDK_PROJECT_PATH="$PWD"

[[ -n "$ANDROID_NDK_HOME" ]] || fail "ANDROID_NDK_HOME not defined."

if [[ ! -e jni/dyncall ]]; then
    ln -s "$DYNCALL_HOME/dyncall/" jni/dyncall
fi

#ABIS="x86 armeabi mips"
#ABIS="x86 armeabi"

$ANDROID_NDK_HOME/ndk-build $@ || fail "Failed to build Android lib"
#$ANDROID_NDK_HOME/ndk-build APP_ABI=$ABI $@ || fail "Failed to build Android lib for ABI $ABI"

if [[ "$@" != "clean" ]]; then
	for OUT_DIR in obj/local/* ; do
	  if [[ -d $OUT_DIR && -f $OUT_DIR/libbridj.so ]]; then
	  	ABI=`basename $OUT_DIR`
	  	ABI_DIR=$LIBS_DIR/$ABI
	  	[[ -d "$ABI_DIR" ]] || mkdir -p "$ABI_DIR"
      cp $OUT_DIR/libbridj.so $ABI_DIR
    fi
	done
fi

rm jni/dyncall || fail "Failed to remove jni/dyncall symlink"
