#!/bin/bash

function fail {
    echo "#" >&2
    echo "# ERROR: $@" >&2
    echo "#" >&2
    exit 1
}

function downloadMaven {
    ARTIFACT=$1
    DEST=$2
    
    #GROUP_ID=$1
    #ARTIFACT_ID=$2
    #VERSION=$3
    #CLASSIFIER=$4
    #PACKAGING=${5:-jar}
    #DEST=${6:-.}
    
    if [[ $VERSION =~ .*-SNAPSHOT ]]; then
        REPO="https://oss.sonatype.org/content/groups/public/"
    else
        REPO="http://repo1.maven.org/maven2/"
    fi
    
    [[ -d "$DEST" ]] || mkdir -p "$DEST" || fail "Cannot create destination dir '$DEST'" 
    mvn org.apache.maven.plugins:maven-dependency-plugin:2.7:get \
        -Dartifact=$ARTIFACT \
        -DrepoUrl=$REPO \
        -Ddest=$DEST || fail "Failed to get artifact '$ARTIFACT' from repo '$REPO'"
}

BRIDJ_VERSION=0.6.3-SNAPSHOT
JNAERATOR_VERSION=0.12-SNAPSHOT

downloadMaven com.nativelibs4java:bridj:$BRIDJ_VERSION:zip:android .
downloadMaven com.nativelibs4java:jnaerator:$JNAERATOR_VERSION:jar:shaded bin

unzip bridj-$BRIDJ_VERSION-android.zip || fail "Failed to unzip BridJ Android distribution"


