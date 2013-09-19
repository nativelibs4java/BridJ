#!/bin/bash
#
# Run GNU or BSD make with some useful environment variables:
# - OS_ARCH gets the value expected by BridJ (linux_x64, win32, darwin_universa...)
# - CONFIG is set to release or debug
# - OS is set to lower-case simple OS name (linux, windows, freebsd...)
# - ARCH is set to x86, x64, armhf, armel...
#

ARCH=`uname -m`
case $ARCH in
i86pc|i386|i486|i586|i686|x86)
	ARCH=x86
	;;
x86_64|amd64)
	ARCH=x64
	;;
esac

OS=`uname -s | sed s/_NT-.*/_NT/`
OS_ARCH=""
case $OS in
WindowsNT)
	case $ARCH in
	x86)
		OS_ARCH=win32
		;;
	x64)
		OS_ARCH=win64
		;;
	esac
	OS="windows"
	;;
Darwin)
	OS="darwin"
	OS_ARCH="darwin_universal"
	;;
Linux)
	case $ARCH in
	arm32)
		if [[ -d /lib/arm-linux-gnueabihf ]] ; then
		  # ARM with hard-float ABI.
      ARCH="armhf"
    elif [[ -d /lib/arm-linux-gnueabi ]] ; then
    	# ARM with soft-float ABI.
      ARCH="armel"
  	else
  		failed "Unknown Linux arm flavour: please update $0"
		fi
		;;
	esac
	OS="linux"
	;;
SunOS)
	OS="sunos"
	;;
OpenBSD)
	OS="openbsd"
	;;
FreeBSD|GNU/kFreeBSD)
	OS="freebsd"
	;;
NetBSD)
	OS="netbsd"
	;;
esac
OS_ARCH=${OS_ARCH:-${OS}_${ARCH}}

# Build release unless DEBUG=1
if [[ "$DEBUG" == "1" ]]; then
	CONFIG=debug
else
	CONFIG=release
fi

if [[ -z "$MAKE_CMD" ]]; then
	MAKE_CMD=make
	# On Solaris, force GNU make if available.
	if [[ "`which gmake`" != "" ]]; then
		MAKE_CMD=gmake
	fi
fi

set -e

cd `dirname $0`
export TOP=${TOP:-$PWD}
export OS
export ARCH
export OS_ARCH
export CONFIG
export OBJ_BASE_DIR=${OBJ_BASE_DIR:-$TOP/target}

if [[ "$ECHO" == "1" ]]; then
	echo "OS = $OS"
	echo "ARCH = $ARCH"
	echo "OS_ARCH = $OS_ARCH"
	echo "CONFIG = $CONFIG"
	exit 0
fi

# You can choose a different resource path,
# see https://code.google.com/p/bridj/wiki/LibrariesLookup
LIBS_PATH=lib

function buildProjects() {
	BASE=$1
	shift
	for D in $BASE/* ; do
		if [[ -d "$D" ]]; then
			cd $D
			$MAKE_CMD $@
		fi
	done
}

# Build main projects (one per native folder).
export OUT_BASE_DIR=${OUT_BASE_DIR:-$TOP/src/main/resources/$LIBS_PATH}
buildProjects $TOP/src/main/native/ $@

# Build test projects.
export OUT_BASE_DIR=${OUT_BASE_DIR:-$TOP/src/test/resources/$LIBS_PATH}
buildProjects $TOP/src/test/native/ $@
