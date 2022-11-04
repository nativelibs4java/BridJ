# BridJ

[![Maven Central](http://maven-badges.herokuapp.com/maven-central/com.nativelibs4java/bridj/badge.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.nativelibs4java%22%20AND%20a%3A%22bridj%22) [![Build Status (Travis: Linux)](https://travis-ci.org/nativelibs4java/BridJ.svg?branch=master)](https://travis-ci.org/nativelibs4java/BridJ) [![Build Status (AppVeyor: Windows)](https://img.shields.io/appveyor/ci/ochafik/bridj/master.svg?label=windows build)](https://ci.appveyor.com/project/ochafik/bridj/) [![Join the chat at https://gitter.im/nativelibs4java/BridJ](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/nativelibs4java/BridJ?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) 

[BridJ](http://bridj.googlecode.com) is a Java / native interoperability library that focuses on speed and ease of use.

It is similar in spirit to [JNA](https://github.com/twall/jna) (dynamic bindings that don't require any native compilation, unlike JNI), but was designed to support C++, to be blazing fast (thanks to [dyncall](http://dyncall.org) + hand-optimized assembly tweaks) and to use modern Java features.

A comprehensive documentation is available on its [Wiki](https://code.google.com/p/bridj/wiki/FAQ?tm=6) (needs migration to GitHub Pages!).

It was previously hosted on [ochafik/nativelibs4java](http://github.com/ochafik/nativelibs4java).

# Quick links

* [Usage](https://code.google.com/p/bridj/wiki/Download) (also see `Examples/BasicExample`)
* [FAQ](https://code.google.com/p/bridj/wiki/FAQ?tm=6)
* [CHANGELOG](./CHANGELOG.md)
* [JavaDoc](http://nativelibs4java.sourceforge.net/bridj/api/development/)
* [Credits and License](http://code.google.com/p/bridj/wiki/CreditsAndLicense)

# Building

```bash
git clone http://github.com/nativelibs4java/BridJ.git
cd BridJ
mvn clean install
```

Iterate on native code:
```bash
mvn native:javah
./BuildNative && mvn surefire:test
```

Build M1 (ARM) binary from Intel Mac (and vice versa)
```bash
# Get both ARM and Intel JDKs
( \
  cd .. && \
    wget https://download.java.net/java/GA/jdk19/877d6127e982470ba2a7faa31cc93d04/36/GPL/openjdk-19_macos-{x64,aarch64}_bin.tar.gz && \
    tar zxvf openjdk-19_macos-aarch64_bin.tar.gz && mv jdk-19.jdk{,-darwin_arm64} && \
    tar zxvf openjdk-19_macos-x64_bin.tar.gz && mv jdk-19.jdk{,-darwin_x64} \
)

# Built both in one go:
export JAVA_HOME_X64=$PWD/../jdk-19.jdk-darwin_x64/Contents/Home
export JAVA_HOME_ARM64=$PWD/../jdk-19.jdk-darwin_arm64/Contents/Home
ARCH=all ./BuildNative

# Or separately:
ARCH=x64 ./BuildNative -DFORCE_JAVA_HOME=$PWD/../jdk-19.jdk-darwin_x64/Contents/Home
ARCH=arm64 ./BuildNative -DFORCE_JAVA_HOME=$PWD/../jdk-19.jdk-darwin_arm64/Contents/Home
```

Build ARM64 binary on Windows X86:
```bash
( \
  cd .. && \
    wget https://github.com/microsoft/openjdk-aarch64/releases/download/jdk-16.0.2-ga/microsoft-jdk-16.0.2.7.1-linux-aarch64.tar.gz && \
    tar zxvf openjdk-19_macos-aarch64_bin.tar.gz && mv jdk-16.0.2+7{,-windows_arm64}
)

# Built both in one go:
export JAVA_HOME_X64=$JAVA_HOME
export JAVA_HOME_ARM64=$PWD/../jdk-16.0.2+7-windows_arm64
ARCH=all ./BuildNative

# Or separately:
ARCH=x64 ./BuildNative
ARCH=arm64 ./BuildNative -DFORCE_JAVA_HOME=$PWD/../jdk-16.0.2+7-windows_arm64
```

Build Windows w/ mingw-64 on Ubuntu/Debian/Mac:
```bash
# First, install the compiler:
#   sudo apt install mingw-w64
#   brew install mingw-w64

( \
  cd .. && \
    wget https://download.java.net/java/GA/jdk19/877d6127e982470ba2a7faa31cc93d04/36/GPL/openjdk-19_windows-x64_bin.zip && \
    unzip openjdk-19_windows-x64_bin.zip && mv jdk-19{,-windows_x64}
)

OS=windows ARCH=x64 ./BuildNative \
  -DCMAKE_TOOLCHAIN_FILE=$PWD/mingw-w64-x86_64.cmake \
  -DFORCE_JAVA_HOME=$PWD/../jdk-19-windows_x64
```

# Debugging

```bash
mvn dependency:build-classpath -DincludeScope=test -Dmdep.outputFile=deps-classpath-test.txt
DEBUG=1 mvn clean test-compile

# Or gdb --args java ...
lldb -- java -cp \
  target/generated-resources:target/generated-test-resources:target/test-classes:target/classes:$( cat deps-classpath-test.txt ) \
  org.junit.runner.JUnitCore \
  org.bridj.BridJTest
```

# Formatting

```
mvn format
```

# Support

Please use the [mailing-list](https://groups.google.com/forum/#!forum/nativelibs4java) and [file bugs](https://github.com/ochafik/nativelibs4java/issues/new).

# TODO

* Separate ARM / x86_64 builds on Darwin (link w/ ARM JDK https://jdk.java.net/archive/), or scripting to create fat libjvm.dylib and libjawt.dylib
* Update pom to make it independent from nativelibs4java-parent
* Update deps: ASM 5.x, JUnit 4.11
* Fix BridJ's armhf support
