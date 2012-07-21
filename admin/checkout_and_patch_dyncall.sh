#!/bin/bash
#
# This script checkouts dyncall (http://dyncall.org/), applies BridJ-specific patches to it and builds it.
# Usage : patch_dyncall.sh directory
#

if [[ -z "$1" ]] ; then
	echo "No name for the dyncall checkout directory"
	exit 1 ;
fi

if [[ -z "$2" ]] ; then
	echo "No path to the diff"
	exit 1 ;
fi

if [[ -d "$DYNCALL_HOME" ]] ; then
	echo "Found DYNCALL_HOME = $DYNCALL_HOME"
	exit 0
fi

DYNCALL_HOME=`pwd`/$1
PATCH_FILE=`pwd`/$2

if [[ -d "$DYNCALL_HOME" ]] ; then
	echo "Directory $DYNCALL_HOME already exists."
	echo "Please backup or remove with 'rm -fR $DYNCALL_HOME' and retry (or use a different name)"
	exit 1 ;
fi

echo "Checking out dyncall to $DYNCALL_HOME..."
svn co https://dyncall.org/svn/dyncall/trunk $DYNCALL_HOME
cd $DYNCALL_HOME

echo "Applying BridJ's dyncall patches..."
gpatch -i $PATCH_FILE -N -p0 || patch -i $PATCH_FILE -N -p0 || ( rm -fR $DYNCALL_HOME && echo "Patch failed, deleted $DYNCALL_HOME" && exit 1 )

echo "Ensuring all diffed files are added to SVN..."
svn add `find . -type f | grep -v .svn` 2> /dev/null || ( echo "Failed to add svn files" && exit 1 ) 
