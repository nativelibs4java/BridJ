#!/bin/bash
set -e

cd `dirname $0`
TOP=$PWD

for D in src/main/native/* ; do
	cd $TOP/$D
	TOP=$TOP $TOP/Build.sh $@
done
