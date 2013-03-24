#!/bin/bash

# Avoid locale in diff :
LANG=C

SRC_HOME=${SRC_HOME:-~/src}
BIN_HOME=${BIN_HOME:-~/bin}

cd $(dirname $0)
BRIDJ_CPP_DIR=$PWD
SCRIPTS_DIR=$PWD/../../../../../scripts

#BUILD_CONFIG=debug sh MakeAll.sh clean 
export MAKE_CMD=make
if [[ "`which gmake`" != "" ]] ; then
	export MAKE_CMD=gmake ;
fi

if [[ "$DEBUG" == "1" ]] ; then
	export OUT_PATTERN=debug ;
else
	export OUT_PATTERN=release ;
fi
	
CURR="`pwd`"
LD=gcc
COMPILE_PIC=1
BUILD_DIR=

function fail() {
	echo "#"
	echo "# ERROR: $@"
	echo "#"
	exit 1
}

#echo BUILD_DIR = $BUILD_DIR
#echo BUILD_CONFIG = $BUILD_CONFIG
#echo LINK_DIRS = $LINK_DIRS

#echo $DYNCALL_HOME/dyncall/$BUILD_DIR


#svn diff $SRC_HOME/dyncall/dyncall > dyncall.diff

cd $DYNCALL_HOME
[[ -f $SCRIPTS_DIR/svnDiffSorted ]] || fail "no svn diff script"
$SCRIPTS_DIR/svnDiffSorted dyncall | sed "s/${DYNCALL_HOME//\//\\/}\///" > $BRIDJ_CPP_DIR/dyncall.diff
#svn diff $DYNCALL_HOME/dyncall | sed "s/${DYNCALL_HOME//\//\\/}\///" > dyncall.diff
#svn diff $SRC_HOME/dyncall/dyncall | sed "s/${HOME//\//\\/}\/src\/dyncall\///" | sed -E 's/^(---|\+\+\+)(.*)\(([^)]+)\)/\1\2/' > dyncall.diff

echo "# Configuring dyncall"
cd "$DYNCALL_HOME/dyncall" || fail "Cannot go to DYNCALL_HOME = $DYNCALL_HOME"

export PATH=/Developer-old/usr/bin:$PATH
if [[ -d /System/Library/Frameworks/ && ! -d /Applications/MobilePhone.app ]] ; then
    # Avoid LC_DYLD_INFO (https://discussions.apple.com/thread/3197542?start=0&tstart=0)
    export MACOSX_DEPLOYMENT_TARGET=10.4
    sh ./configure --target-universal || fail "Failed to configure MacOS X Universal build"
else 
    sh ./configure || fail "Failed to configure default build"
fi

if [[ -z "$SHAREDLIB_SUFFIX" ]] ; then
	if [[ -d /System/Library/Frameworks/ ]] ; then
		SHAREDLIB_SUFFIX=dylib ;
	else 
		SHAREDLIB_SUFFIX=so ;
	fi ;
fi

echo "# Making dyncall with '$MAKE_CMD $@'"
$MAKE_CMD $@ || fail "Failed to make dyncall"

echo "# Making BridJ"
cd "$CURR"
$MAKE_CMD $@ || fail "Failed to make BridJ"

echo "# Making test library"
cd "../../../test/cpp/test"
$MAKE_CMD $@ || fail "Failed to make BridJ's test library" ;

echo "# Making dependsOnTest library"
cd "../../../test/cpp/dependsOnTest"
$MAKE_CMD $@ || fail "Failed to make BridJ's dependsOnTest library" ;

cd "$CURR"

if [[ -d build_out ]] ; then
	cd build_out

	for D in `ls . | grep _$OUT_PATTERN` ; do
		ARCH_NAME="`echo $D| sed "s/_gcc_$OUT_PATTERN//"| sed "s/_androidndk_$OUT_PATTERN//"`"
		if [[ "$ARCH_NAME" == "android_arm32_arm" ]] ; then
			RES_SUB="libs/armeabi" ;
		elif [[ "$ARCH_NAME" == "linux_arm32_arm" && -d /lib/arm-linux-gnueabihf ]] ; then
                        RES_SUB="org/bridj/lib/linux_armhf" ;
                elif [[ "$ARCH_NAME" == "linux_arm32_arm" && -d /lib/arm-linux-gnueabi ]] ; then
                        RES_SUB="org/bridj/lib/linux_armel" ;
                else
			RES_SUB="org/bridj/lib/$ARCH_NAME" ;
		fi
		MAIN_OUT="../../../resources/$RES_SUB"
	
		echo ARCH_NAME: $ARCH_NAME
		echo RES_SUB: $RES_SUB
		TEST_OUT="../../../../test/resources/$RES_SUB"
	
		mkdir -p $MAIN_OUT
		cp $D/*.$SHAREDLIB_SUFFIX $MAIN_OUT
		
        mkdir -p $TEST_OUT 
        cp ../../../../test/cpp/test/build_out/$D/*.$SHAREDLIB_SUFFIX $TEST_OUT
        cp ../../../../test/cpp/dependsOnTest/build_out/$D/*.$SHAREDLIB_SUFFIX $TEST_OUT
    
        nm $TEST_OUT/*.so > $TEST_OUT/test.so.nm
        nm $TEST_OUT/*.dylib > $TEST_OUT/test.dylib.nm ;
		
		echo "Done for $D" ;
	#	svn add $MAIN_OUT
	#	svn add $TEST_OUT ;
	done ;
fi
