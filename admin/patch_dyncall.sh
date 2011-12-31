#!/bin/bash
#
# This script checkouts dyncall (http://dyncall.org/), applies BridJ-specific patches to it and builds it.
# Usage : patch_dyncall.sh directory
#

if [[ -z "$1" ]] ; then
	echo "Please provide a name for the dyncall checkout directory as first and unique argument"
	exit 1 ;
fi

DYNCALL_HOME=`pwd`/$1

if [[ -d "$DYNCALL_HOME" ]] ; then
	echo "Directory $DYNCALL_HOME already exists."
	echo "Please backup or remove with 'rm -fR $DYNCALL_HOME' and retry (or use a different name)"
	exit 1 ;
fi

echo "Checking out dyncall to $DYNCALL_HOME..."
svn co https://dyncall.org/svn/dyncall/trunk $DYNCALL_HOME
cd $DYNCALL_HOME

if [[ "$NO_PATCH" != "1" ]] ; then
	echo "Retrieving BridJ's dyncall patches..."
	svn export https://nativelibs4java.googlecode.com/svn/trunk/libraries/Runtime/BridJ/src/main/cpp/bridj/dyncall.diff
	echo "Applying BridJ's dyncall patches..."
	gpatch -i dyncall.diff -N -p0 || patch -i dyncall.diff -N -p0 ;
fi

cd dyncall
echo "Configuring..."
if [[ -d /System/Library/Frameworks/ ]] ; then sh ./configure --target-universal ; 
else sh ./configure ; fi

echo "Building..."
make clean
make

echo "Listing build results :"
find $DYNCALL_HOME/dyncall/*/build_out
