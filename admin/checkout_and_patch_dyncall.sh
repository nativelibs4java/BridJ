#!/bin/bash
#
# This script checkouts dyncall (http://dyncall.org/), applies BridJ-specific patches to it and builds it.
# Usage : patch_dyncall.sh directory
#

#set -e

function failed() {
	echo "$@"
	exit 1
}

if [[ -z "$DYNCALL_HOME" ]] ; then
	failed "DYNCALL_HOME not set"
fi

if [[ -d "$DYNCALL_HOME" ]] ; then
	failed "DYNCALL_HOME = $DYNCALL_HOME already exists"
fi

if [[ -z "$1" ]] ; then
	failed "No path to the diff"
fi

PATCH_FILE=$1

#if [[ ! -d `dirname $DYNCALL_HOME` ]] ; then
#	mkdir `dirname $DYNCALL_HOME` || failed "Failed to create parent directory for $DYNCALL_HOME"
#fi

#echo "Checking out dyncall to $DYNCALL_HOME..."
svn co http://dyncall.org/svn/dyncall/trunk $DYNCALL_HOME || failed "Failed to checkout dyncall to $DYNCALL_HOME" 
cd $DYNCALL_HOME || failed "Failed to go to $DYNCALL_HOME"

echo "Applying BridJ's dyncall patches..."

if [ ! `which gpatch` ] ; then 
	PATCH_CMD=patch
else
	PATCH_CMD=gpatch
fi

$PATCH_CMD -i $PATCH_FILE -N -p0
# $PATCH_CMD -i $PATCH_FILE -N -p0 || ( rm -fR $DYNCALL_HOME && failed "Patch failed, deleted $DYNCALL_HOME" )

echo "Ensuring all diffed files are added to SVN..."
svn add `find . -type f | grep -v .svn` 2> /dev/null 

echo "Displaying svn status..."
svn status

