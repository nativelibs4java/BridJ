# BridJ

[BridJ](http://bridj.googlecode.com) is a Java / native interoperability library that focuses on speed and ease of use.

It is similar in spirit to [JNA](https://github.com/twall/jna) (dynamic bindings that don't require any native compilation, unlike JNI), but was designed to support C++, to be blazing fast (thanks to [dyncall](http://dyncall.org) + hand-optimized assembly tweaks) and to use modern Java features.

A comprehensive documentation is available on its [Wiki](https://code.google.com/p/bridj/wiki/FAQ?tm=6).

It was previously hosted on [ochafik/nativelibs4java](http://github.com/ochafik/nativelibs4java).

# Quick links

* [Usage](https://code.google.com/p/bridj/wiki/Download) (also see `Examples/BasicExample`)
* [FAQ](https://code.google.com/p/bridj/wiki/FAQ?tm=6)
* [CHANGELOG](https://github.com/ochafik/BridJ/blob/master/CHANGELOG)
* [JavaDoc](http://nativelibs4java.sourceforge.net/bridj/api/development/)
* [Credits and License](http://code.google.com/p/bridj/wiki/CreditsAndLicense)

# Building
  ```
  git clone http://github.com/ochafik/BridJ.git
  mvn clean install
  ```

# Support

Please use the [mailing-list](https://groups.google.com/forum/#!forum/nativelibs4java) and [file bugs](https://github.com/ochafik/nativelibs4java/issues/new).

# TODO

* Update pom to make it independent from nativelibs4java-parent
* Update deps: ASM 5.x, JUnit 4.11
* Fix BridJ's armhf support
