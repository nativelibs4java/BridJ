#!/bin/bash
set -eu

readonly PLATFORM=$1
readonly FROM_IMAGE=$2
readonly TO_IMAGE=$3
shift 3

if [[ "${BUILD_IMAGE:-1}" == "1" ]]; then
  echo "
    FROM $FROM_IMAGE
    RUN --mount=type=cache,target=/var/cache/apt,sharing=locked \
        --mount=type=cache,target=/var/lib/apt,sharing=locked \
        apt-get update -qy && \
        apt-get install -qy --no-install-recommends \
          maven cmake make gcc g++ openjdk-17-jdk && \
        echo \"export JAVA_HOME=/usr/lib/jvm/\$( ls /usr/lib/jvm | grep java-17-openjdk- )\" >> ~/.profile

    WORKDIR /root/BridJ
  " | docker build -f- . -t $TO_IMAGE
fi

docker run \
  --platform $PLATFORM \
  -e OS \
  -e ARCH \
  -v ${M2_DIR:-$HOME/.m2}:/root/.m2 \
  -v ${BRIDJ_DIR:-$PWD}:/root/BridJ \
  --rm -it $TO_IMAGE \
  /bin/bash -c "source ~/.profile ; ./BuildNative $* && mvn surefire:test"
