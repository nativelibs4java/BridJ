#!/bin/bash

function fail {
    echo "#" >&2
    echo "# ERROR: $@" >&2
    echo "#" >&2
    exit 1
}

cd $(dirname $0)

find . -name '*~' -exec rm '{}' ';'
rm -fR bin libs obj *.zip
rm LICENSE.BridJ.txt README.BridJ.txt
