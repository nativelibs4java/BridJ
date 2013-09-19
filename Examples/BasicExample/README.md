= BridJ &amp; JNAerator Example =

This sample project shows how BridJ &amp; JNAerator can be used to maintain a custom native library built on many platforms, along with its bindings.

= Building =

To build on Unix (MacOS X, Linux, Solaris), simply type the following lines
```
./BuildAll.sh clean && ./BuildAll.sh
mvn clean install
```

This will automatically:
* Rebuild the native library for the host architecture (`libexample.so` or `libexample.dylib`)
* Create the bindings under `target/generated-sources`
* Compile all the Java files under `src/main/java` (you can put your own there, for instance to wrap C/C++ entities in Java ones) and create a JAR in `target`
* Run the tests in `src/test/java`

= Customizing =

You may want to:
* Add more `*.cpp` files under `src/main/native/example`: just add them in `src/main/native/example/Makefile` with a `UNITS += mynewfile` line, without the file extension.
* Add more libraries under `src/main/native` (each directory will need its own `Makefile` that can be copied from the `example` one)
* Tweak the JNAerator config file `src/main/jnaerator/config.jnaerator` (see [JNAerator Wiki](https://code.google.com/p/jnaerator/wiki/CommandLineOptionsAndEnvironmentVariables))
* Port the build scripts and/or BridJ to a new architecture (platforms supported by [dyncall](http://dyncall.org/) should be extremely easy to port)

You may want to inspect `pom.xml` to update to a newer release of BridJ or JNAerator.
