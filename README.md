# BridJ

[![Maven Central](https://img.shields.io/maven-central/v/com.nativelibs4java/bridj.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.nativelibs4java%22%20AND%20a%3A%22bridj%22) [![Build Status (Travis: Linux)](https://img.shields.io/travis/nativelibs4java/BridJ.svg?label=linux%20build)](https://travis-ci.org/nativelibs4java/BridJ) [![Build Status (AppVeyor: Windows)](https://img.shields.io/appveyor/ci/ochafik/bridj/master.svg?label=windows build)](https://ci.appveyor.com/project/ochafik/bridj/) [![Join the chat at https://gitter.im/nativelibs4java/BridJ](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/nativelibs4java/BridJ?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) 

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
  ```
  git clone http://github.com/nativelibs4java/BridJ.git
  mvn clean install
  ```

# Formatting

```
mvn format
```

# Support

Please use the [mailing-list](https://groups.google.com/forum/#!forum/nativelibs4java) and [file bugs](https://github.com/ochafik/nativelibs4java/issues/new).

# TODO

* Update pom to make it independent from nativelibs4java-parent
* Update deps: ASM 5.x, JUnit 4.11
* Fix BridJ's armhf support
