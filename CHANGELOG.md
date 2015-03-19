## Current development version (0.7.1-SNAPSHOT)

...

## BridJ 0.7.0 (20150308)

- Added LRU pointer cache (disable with -Dbridj.cache.pointers=false or BRIDJ_CACHE_POINTERS=0), which helps keep short-lived pointer garbage to a minimum for some use cases (see [issue nativelibs4java#440]
  (https://github.com/nativelibs4java/nativelibs4java/issues/440))
- Added experimental support for Raspberry Pi (Linux/armhf, hard-float ABI).
- Added a new all-in-one android release zip with classes JAR (now contains no native lib), native libs, sources &amp; javadoc.
- Added programmatic setting of library dependencies: BridJ.addNativeLibraryDependencies ([issue nativelibs4java#424]
  (https://github.com/nativelibs4java/nativelibs4java/issues/424)),  -Dbridj.Xyz.dependencies=Abc,Def and BRIDJ_XYZ_DEPENDENCIES=Abc,Def (issue #391)
- Added a very useful BRIDJ_DEBUG_POINTER_RELEASES=1 / -Dbridj.debug.pointer.releases=true mode that helps track double releases (also enabled when BRIDJ_DEBUG_POINTERS=1 / -Dbridj.debug.pointers=true).
- Added Pointer.pointerToAddress(peer, size, io, release)
- Added fallback to GCC demangler for mingw32 on Windows ([issue nativelibs4java#356]
  (https://github.com/nativelibs4java/nativelibs4java/issues/356)).
- Added support for exact library file name ([issue nativelibs4java#424]
  (https://github.com/nativelibs4java/nativelibs4java/issues/424))
- Added Pointer.pointerTo(IntValuedEnum&lt;E&gt;) ([issue nativelibs4java#414]
  (https://github.com/nativelibs4java/nativelibs4java/issues/414))
- Added support for C++ namespaces ([issue nativelibs4java#446]
  (https://github.com/nativelibs4java/nativelibs4java/issues/446))
- Added support for @Name annotations on structs and enums (allows refactoring of such classes, [issue nativelibs4java#443]
  (https://github.com/nativelibs4java/nativelibs4java/issues/443))
- Added support for new JNAerator-generated crossed callbacks (one with an object-heavy signature, and one with raw types, each calling the other, one of the two being overridden)
- Dropped C# runtime stubs
- Dropped linux-only and mac-only packages.
- Improved performance of all operations of Pointers with bounds (~ 25% faster).
- Improved concurrency of callbacks and structs creation.
- Improved logs ([issue nativelibs4java#328]
  (https://github.com/nativelibs4java/nativelibs4java/issues/328), [issue nativelibs4java#346]
  (https://github.com/nativelibs4java/nativelibs4java/issues/346)).
- Improved C++ templates (still WIP, std::list protoype and fixed std::vector)
- Improved Grails integration with Platform.getClassLoader change ([issue nativelibs4java#431]
  (https://github.com/nativelibs4java/nativelibs4java/issues/431))
- Improved PointerIO caching.
- Refactored StructIO (split out description of struct and its fields as top-level entities, StructIO now just does IO). This is what justifies version bump.
- Rebuilt Linux binaries with --hash-style=both for better backwards compatibility ([issue nativelibs4java#436]
  (https://github.com/nativelibs4java/nativelibs4java/issues/436))
- Rebuilt Linux binaries with -z noexecstack
- Rebuilt Unix libraries with -fno-stack-protector, and force GLIBC dependency down to 2.3.4 ([issue nativelibs4java#467]
  (https://github.com/nativelibs4java/nativelibs4java/issues/467))
- Rebuilt Solaris binaries with statically linked libgcc ([issue nativelibs4java#452]
  (https://github.com/nativelibs4java/nativelibs4java/issues/452))
- Fixed C++ demangling of int vs. pointer ([issue nativelibs4java#482]
  (https://github.com/nativelibs4java/nativelibs4java/issues/482))
- Fixed bit fields in structs! ([issue nativelibs4java#496]
  (https://github.com/nativelibs4java/nativelibs4java/issues/496))
- Fixed Windows c library alias: "msvcrt", not "mscvrt" ([issue nativelibs4java#492]
  (https://github.com/nativelibs4java/nativelibs4java/issues/492), thanks to @rkraneis for the patch!)
- Fixed multithreading crashes due to strerror ([issue nativelibs4java#450]
  (https://github.com/nativelibs4java/nativelibs4java/issues/450))
- Fixed @Alignment annotation
- Fixed demangling of pointer types (now stricter matching of target types, with support for callbacks).
- Fixed support for Android/arm: artifact of last two releases lacked the binary, and had to move android libs from lib/ to libs/ ([issue nativelibs4java#382]
  (https://github.com/nativelibs4java/nativelibs4java/issues/382))
- Fixed usage of local refs in getLibrarySymbols to avoid reaching Dalvik's limit ([issue nativelibs4java#376]
  (https://github.com/nativelibs4java/nativelibs4java/issues/376))
- Fixed openjdk library path (take it from sun.boot.library.path)
- Fixed VARIANT memory management: use VariantInit and VariantClear + allocate it (and other structs tagged with COMRuntime) with CoTaskMemAlloc (see [issue nativelibs4java#389]
  (https://github.com/nativelibs4java/nativelibs4java/issues/389))
- Fixed typo in EllipsisHelper that broke some varargs
- Fixed loading of dependent libraries on Windows using LoadLibraryEx + LOAD_WITH_ALTERED_PATH ([issue nativelibs4java#378]
  (https://github.com/nativelibs4java/nativelibs4java/issues/378))
- Fixed binding of c library on windows
- Fixed pointerToCStrings: don't update string array (might be causing / aggravating #397)
- Fixed native library lookup logic ([issue nativelibs4java#406]
  (https://github.com/nativelibs4java/nativelibs4java/issues/406))
- Fixed NPE in DefaultParameterizedType.hashCode ([issue nativelibs4java#411]
  (https://github.com/nativelibs4java/nativelibs4java/issues/411))
- Fixed handling of @Name when demangler goes nuts ([issue nativelibs4java#413]
  (https://github.com/nativelibs4java/nativelibs4java/issues/413))
- Fixed FlagSet.toString() and had FlagSet.fromValue(int, E[]) to return raw enum when possible instead of always a FlagSet ([issue nativelibs4java#414]
  (https://github.com/nativelibs4java/nativelibs4java/issues/414))
- Fixed alignment on 32-bit linux ([issue nativelibs4java#320]
  (https://github.com/nativelibs4java/nativelibs4java/issues/320))
- Fixed warnings about missing vtables for COM objects ([issue nativelibs4java#355]
  (https://github.com/nativelibs4java/nativelibs4java/issues/355))
- Fixed disappearing MFC &amp; STL classes in artifacts ([issue nativelibs4java#392]
  (https://github.com/nativelibs4java/nativelibs4java/issues/392))
- Fixed some GCC demangling shortcut cases like repeated const pointers.
- Documented effects of protected mode ([issue nativelibs4java#394]
  (https://github.com/nativelibs4java/nativelibs4java/issues/394))
- Documented DefaultParameterizedType.paramType (see [issue nativelibs4java#418]
  (https://github.com/nativelibs4java/nativelibs4java/issues/418))
...

## BridJ 0.6.2 (20130107)

- Fixed serious crashes on Win64 in assembler optimizations
- Fixed BridJ.protectFromGC !
- Fixed raw assembler optimization for floats &amp; doubles, finally! (+ updated Win binaries)
- Fixed handling of classloaders in some use-cases ([issue nativelibs4java#283]
  (https://github.com/nativelibs4java/nativelibs4java/issues/283))
- Fixed Platform.open(File) ([issue nativelibs4java#306]
  (https://github.com/nativelibs4java/nativelibs4java/issues/306))
- Fixed Pointer.copyTo(dest, elementCount) ([issue nativelibs4java#317]
  (https://github.com/nativelibs4java/nativelibs4java/issues/317))
- Fixed alignment of struct array fields ([issue nativelibs4java#319]
  (https://github.com/nativelibs4java/nativelibs4java/issues/319))
- Fixed alignment of double fields on Linux 32 bits ([issue nativelibs4java#320]
  (https://github.com/nativelibs4java/nativelibs4java/issues/320))
- Fixed dlopen log for non existing absolute library paths
- Added experimental Linux/arm support ([issue nativelibs4java#327]
  (https://github.com/nativelibs4java/nativelibs4java/issues/327))
- Added @Library.dependencies + test library
- Added BridJ.getNativeLibraryName back
- Added ComplexDouble struct for C99's `_Complex double` type.
- Added GCC shortcut case for demangling of C++ constructors 
- Added Pointer.getIntAtIndex(long) / .setIntAtIndex(long, int) (and with all primitive variants)
- Added quiet mode (BRIDJ_QUIET=1 / bridj.quiet=true) ([issue nativelibs4java#328]
  (https://github.com/nativelibs4java/nativelibs4java/issues/328))
- Added assembler optimizations for functions with up to 16 arguments on Win64 !
- Added parsing of Mach-O compressed symbols tries (LC_DYLD_INFO command) to dyncall ([issue nativelibs4java#311]
  (https://github.com/nativelibs4java/nativelibs4java/issues/311))
- Added Pointer.withoutValidityInformation() (yields faster, unsafe pointer)
- Added BridJ.subclassWithSynchronizedNativeMethods(Class) to create a subclass where all native methods are overridden
- Added Pointer.getIntAtIndex(long) / .setIntAtIndex(long, int) (and with all primitive variants)
- Added naive OSGi support to the main JAR.
- Rationalized Java logs ([issue nativelibs4java#328]
  (https://github.com/nativelibs4java/nativelibs4java/issues/328))
- Changed library extraction mechanism to allow extraction of dependencies (see @Library.dependencies); removed DeleteOldBinaries option
- Special aliases for "c" and "m" libraries on windows (-&gt; mscvrt)
- Speedup assembler optimization on win64 (movsb -&gt; movsq)
- Removed ios-package (binaries for iOS/arm) 

## BridJ 0.6.1 (20120415, commit 6bc061dfce06b941086a29f696195e82fbaffbdc) 

- Release artifacts are available in Maven Central
- Fixed wchar_t and  WideCString on MacOS X ([issue nativelibs4java#295]
  (https://github.com/nativelibs4java/nativelibs4java/issues/295))
- Fixed @Name annotation
- Fixed deletion of temporary extracted library files with shutdown hook ([issue nativelibs4java#197]
  (https://github.com/nativelibs4java/nativelibs4java/issues/197))
- Fixed Unix binaries dependency on GLIBC 2.11 (lowered to version 2.2.5) ([issue nativelibs4java#195]
  (https://github.com/nativelibs4java/nativelibs4java/issues/195))
- Fixed some pointer methods : 3D pointers allocation, Pointer.setXXXsAtOffset, float endianness conversion
- Fixed regression on struct-valued struct fields (when implemented by java fields) ([issue nativelibs4java#200]
  (https://github.com/nativelibs4java/nativelibs4java/issues/200))
- Fixed crash with valued enum return values ([issue nativelibs4java#196]
  (https://github.com/nativelibs4java/nativelibs4java/issues/196))
- Fixed crash of dynamic callbacks on Win32
- Fixed attachment of native threads in native -&gt; Java callbacks (should attach them as JVM daemon threads)
- Fixed C++ inner class name-mangling matching
- Fixed signature of ITaskbarList3.SetProgressValue ([issue nativelibs4java#218]
  (https://github.com/nativelibs4java/nativelibs4java/issues/218))
- Fixed Pointer.getByteBuffer() and Pointer.getBytes() (and all their type variants !)
- Fixed StructIO.getEnumField (now returns an IntValuedEnum&lt;E&gt;)
- Fixed handling of CLong / SizeT java fields in structs (cf. [issue nativelibs4java#253]
  (https://github.com/nativelibs4java/nativelibs4java/issues/253))
- Fixed StructIO.FieldDecl accessibility to allow override of all StructIO.Customizer methods (cf. [issue nativelibs4java#220]
  (https://github.com/nativelibs4java/nativelibs4java/issues/220))  
- Fixed regression on libc, created with null path ([issue nativelibs4java#217]
  (https://github.com/nativelibs4java/nativelibs4java/issues/217))
- Fixed back-references in Visual C++ demangler ([issue nativelibs4java#291]
  (https://github.com/nativelibs4java/nativelibs4java/issues/291))
- Fixed binding of IntValuedEnum&lt;E&gt; types
- Fixed handling of virtual destructors with GCC-compiled libraries (and virtual table pointer + runtime guess / check of virtual index for virtual methods) ([issue nativelibs4java#281]
  (https://github.com/nativelibs4java/nativelibs4java/issues/281))
- Fixed shortcuts in GCC4Demangler (contrib from Rmi monet, [issue nativelibs4java#211]
  (https://github.com/nativelibs4java/nativelibs4java/issues/211))
- Added support for PointerIO&lt;IntValuedEnum<E&gt;> ([issue nativelibs4java#261]
  (https://github.com/nativelibs4java/nativelibs4java/issues/261))
- Added ProcessUtils.getCurrentProcessId()
- Allow libname.so and name.so for embedded libraries on Unix ([issue nativelibs4java#215]
  (https://github.com/nativelibs4java/nativelibs4java/issues/215))
- Enhanced multi-threaded performance of PointerIO.getInstance ([issue nativelibs4java#203]
  (https://github.com/nativelibs4java/nativelibs4java/issues/203))
- Added Pointer.getXxxs(XxxBuffer)
- Added alias mechanism for annotations : any annotation which class is itself annotated by @Ptr / @CLong can serve as an alias ([issue nativelibs4java#202]
  (https://github.com/nativelibs4java/nativelibs4java/issues/202))
- Added native error log when LoadLibrary or dlopen failed
- Added IntValuedEnum&lt;E&gt; FlagSet.fromValue(int, E...)
- Enabled all compiler optimizations on all platforms but win32
- Updated doc of Pointer.getXxxs()
- Added experimental by-value struct support on amd64 for structs larger than 64 bits (disabled by default, set -Dbridj.structsByValue=true or BRIDJ_STRUCTS_BY_VALUE=1 to enable)

## BridJ 0.6 (20111107, commit 4950e5c58f32869ce460dbbc59fe969865dd9288)

- Added errno/GetLastError() mechanism : declare methods to throw org.bridj.LastError and it's all handled automatically ([issue nativelibs4java#74]
  (https://github.com/nativelibs4java/nativelibs4java/issues/74))
- Added protected mode (-Dbridj.protected=true / BRIDJ_PROTECTED=1), to prevent native crashes (makes BridJ bindings slower + disables optimized raw calls).
- Added proxy-based Objective-C delegates support (forwards unknown methods to a Java instance) ([issue nativelibs4java#188]
  (https://github.com/nativelibs4java/nativelibs4java/issues/188))
- Added Objective-C 2.0 blocks support (similar to callbacks, inherit from ObjCBlock instead of Callback) ([issue nativelibs4java#192]
  (https://github.com/nativelibs4java/nativelibs4java/issues/192))
- Added Pointer.asList() and .asList(ListType) to get a List view of the pointed memory 
  - depending on the ListType, the view can be mutable / resizeable
  - removed the List interface from Pointer (which is now just an Iterable)
  - added Pointer.allocateList(type, capacity) to create a NativeList from scratch (has a .getPointer method to grab the resulting memory at the end)
- Added Pointer.moveBytesTo(Pointer)
- Added support for embedded libraries extraction from "lib/arch" paths (along with "org/bridj/lib/arch", where arch uses BridJ's convention)
- Added TimeT (time_t), timeval classes ([issue nativelibs4java#72]
  (https://github.com/nativelibs4java/nativelibs4java/issues/72))
- Added Platform.getMachine() (same result as `uname -m`)
- Added support for multiarch Linux distributions ([issue nativelibs4java#2]
  (https://github.com/nativelibs4java/nativelibs4java/issues/2))
- Added support for versioned library file names ([issue nativelibs4java#72]
  (https://github.com/nativelibs4java/nativelibs4java/issues/72))
- Added global allocated memory alignment setting (BRIDJ_DEFAULT_ALIGNMENT env. var. &amp; bridj.defaultAlignment property), + Pointer.allocateAlignedArray
- Added basic calls log mechanism (disables direct mode) : -Dbridj.logCalls=true or BRIDJ_LOG_CALLS=1 (only logs the method name &amp; signature, not the arguments or returned values)
- Added BridJ.setMinLogLevel(Level) ([issue nativelibs4java#190]
  (https://github.com/nativelibs4java/nativelibs4java/issues/190))
- Added Platform.addEmbeddedLibraryResourceRoot(root) to use &amp; customize the embedded library extraction feature in user projects
- Added support for packed structs (@Struct(pack = 1), or any other pack value)
- Added check of BridJ environment variables and Java properties : if any BRIDJ_* env. var. or bridj.* property does not exist, it will log warnings + full list of valid options
- Added @JNIBound annotation to mark native methods that should not be bound by BridJ but by plain old JNI
- Fixed Pointer.next/.offset methods (used to throw errors a the end of iteration)
- Fixed Pointer.getNativeObjectAtOffset(long byteOffset, Type type)
- Fixed struct fields implemented as Java fields
- Fixed resolution of MacOS X "ApplicationServices" framework binaries, such as CoreGraphics
- Fixed some COM bugs with IUnknown
- Fixed demangling/matching of CLong &amp; SizeT
- Fixed CLong &amp; SizeT arguments
- Fixed Objective-C runtime (basic features), added NSString constructor &amp; NSDictionary (with conversion to/from Map&lt;NSString, NSObject&gt;)
- Fixed crashes on Win32 (when using Pointer class in bound function arguments)
- Fixed crash during deallocation of Callbacks + fixed leak of Callbacks (now need to retain a reference to callbacks or use BridJ.protectFromGC / unprotectFromGC)
- Made the StructIO customization mechanism more flexible
- Made JawtLibrary public
- Various Javadoc tweaks

## BridJ 0.5 (r2128, 20110621)

- Added support for Android(arm) platform
- Added Pointer.clone() that duplicates the memory (requires a pointer with bounds information)
- Added various pre-packaged specialized subsets of BridJ : c-only, windows-only, macosx-only, unix-only, linux-only, ios-only, android
- Added Pointer.allocateDynamicCallback(DynamicCallback&lt;R&gt;, callingConv, returnType, paramTypes...)
- Added BridJ native library path override : one can set the BRIDJ_LIBRARY environment variable or the "bridj.library" property to the full path of libbridj.so/.dylib/.dll

## BridJ 0.4.2 (r2009, 20110527)

- Fixed behaviour in environments with a null default classloader (as in Scala 2.9.0) 
- Added support for Java 1.5 ([issue nativelibs4java#57]
  (https://github.com/nativelibs4java/nativelibs4java/issues/57))
- Added support for MacOS X 10.4, 10.5 (was previously restricted to 10.6)

## BridJ 0.4.1 (r1990, 20110513)

- Fixed callbacks on Windows x86
- Fixed multithreaded callbacks ! (callbacks called in a different thread than the one that created them used to hang indefinitely)
- Fixed Pointer and ValuedEnum arguments and return values in callbacks
- Fixed loading of libraries that depend on other libraries in the same directory on Windows ([issue nativelibs4java#65]
  (https://github.com/nativelibs4java/nativelibs4java/issues/65))
- Fixed BridJ.sizeOf(Pointer.class), sizeOf(SizeT.class), sizeOf(CLong.class)...
- Enhanced C++ templates support
- Added support for Windows APIs Unicode vs. ANSI functions renaming (e.g. SendMessage being one of SendMessageW or SendMessageA, depending on Platform.useUnicodeVersionOfWindowsAPIs)
- Added deprecated support for struct fields implemented as Java fields, to ease up migration from JNA (needs manual calls to BridJ.writeToNative(struct) and BridJ.readFromNative(struct)) ([issue nativelibs4java#54]
  (https://github.com/nativelibs4java/nativelibs4java/issues/54))
- Added preliminary read-only support for STL's std::vector&lt;T&gt; C++ type
- Added BridJ.describe(Type) to describe structs layouts (automatically logged for each struct type when BRIDJ_DEBUG=1 or -Dbridj.debug=true)
- Added BridJ.describe(NativeObject).
- Added StructObject.toString() (calls BridJ.describe(this))
- Added BRIDJ_DEBUG_POINTERS=1 (or -Dbridj.debug.pointers=true) to display extended pointer allocation / deallocation debug information
- Reorganized Windows COM packages (moved out DirectX code to it own top-level project : com.nativelibs4java:directx4java)
- Implemented FlagSet.equals

## BridJ 0.4 (r1869, 20110408)

- Added parsing of GNU LD scripts ([issue nativelibs4java#61]
  (https://github.com/nativelibs4java/nativelibs4java/issues/61))
- Fixed demangling of size_t / long C types with GCC
- Fixed Linux x86 symbols
- Added experimental C++ virtual overrides : it is now possible to subclass C++ classes from Java, even with anonymous inner classes ! (no support for multiple inheritance yet)
- Fixed crash in C++ destructors at the JVM shutdown ([issue nativelibs4java#60]
  (https://github.com/nativelibs4java/nativelibs4java/issues/60))
- Fixed callbacks with float args
- Added support for varargs functions
- Fixed size computation of structs with array fields ([issue nativelibs4java#64]
  (https://github.com/nativelibs4java/nativelibs4java/issues/64)) 

## BridJ 0.3.1 (r1817, 20110329)

- Introduced basic C++ templates support (binding of compiled template classes, not template methods / functions yet)
- Added dynamic functions support : Pointer.asDynamicFunction(callConv, returnType, argTypes...)
- Added support for arbitrary C++ constructors
- Added support for __stdcall callbacks
- Added COM VARIANT class with very basic data conversion support
- Added many COM UUID definitions (from uuids.h, codecapi.h, ksuuids.h)
- Added Solaris x86 support
- Added @DisableDirect annotation to force-disable raw assembly optimizations (also see BRIDJ_DIRECT=0 or -Dbridj.direct=false for global disable)
- Fixed long return values ([issue nativelibs4java#47]
  (https://github.com/nativelibs4java/nativelibs4java/issues/47))
- Fixed '@Ptr long' return values on 32 bits platforms
- Fixed structs sub-structs and array fields
- Fixed unions : 
  - pure unions can be created with the @Union annotation on the union class (+ fields annotated with @Field(value = uniqueId))
  - structs with unioned fields can be defined with fields annotated with @Field(value = uniqueId, unionWith = indexOfTheFirstFieldOfTheUnion) 
- Fixed size computation of unions &amp; structs ([issue nativelibs4java#51]
  (https://github.com/nativelibs4java/nativelibs4java/issues/51))
- Fixed JAWTUtils on win32 ([issue nativelibs4java#52]
  (https://github.com/nativelibs4java/nativelibs4java/issues/52))
- Fixed Pointer.pointerToAddress(long, Class, Releaser) ([issue nativelibs4java#48]
  (https://github.com/nativelibs4java/nativelibs4java/issues/48))
- Fixed incomplete refactoring ([issue nativelibs4java#58]
  (https://github.com/nativelibs4java/nativelibs4java/issues/58))
- Moved all the is64Bits(), isWindows()... methods and SIZE_T_SIZE constants out of JNI class into new Platform class
- Moved the C++ symbols demanglers to package org.bridj.demangling
- Renamed Pointer.asPointerTo(Type) to Pointer.as(Type)
- Enhanced FlagSet (added toString(), toEnum(), fromValue(ValuedEnum&lt;E&gt;))
- Enhanced Pointer (added allocate(Type), allocateArray(Type, long))
- Greatly enhanced the API Javadoc

## BridJ 0.3 (r1638, 20110204):

- Fixed binding of "c" library on Unix
- Fixed iteration on unbound native-allocated pointers (issue 37). 
- Fixed Visual C++ demangling (issue 36 : bad handling of back-references).
- Added Pointer.getBuffer(), getSizeTs(), getCLongs() and other missing methods.
- Fixed byteOffset-related issues in CLong and SizeT pointer read/write methods.
- Renamed most pointer byteOffset methods with an -AtOffset suffix (for instance, Pointer.getInt(long) becomes getIntAtOffset(long))
- Inverted charset and StringType arguments in Pointer.getString / .setString methods
- Renamed Pointer.withIO(PointerIO&lt;U&gt;) to Pointer.as(PointerIO<U>)
- Added Pointer.asUntyped() (equiv. to Pointer.as((Class&lt;?&gt;)null))
- Allow pointerToBuffer on a non-direct buffer (and added Pointer.updateBuffer
to copy data back to non-direct buffer if needed)
- Assume @Runtime(CRuntime.class) by default
- Autodetect calling convention on Windows (based on name mangling), unless convention is explicitely specified with @Convention(Style.X)
- Added BRIDJ_&lt;LIBNAME&gt;_LIBRARY environment variables to hard-code the shared library path in priority
- Added library alias mechanism : BridJ.setNativeLibraryActualName, .addNativeLibraryAlias
- Fixed callbacks-related Win32 crashes
- Fixed super-critical bug on Windows 32 bits with size_t arguments !
- Fixed some Pointer endianness bugs

## BridJ 0.2 (r1330, 20101011):

- Renamed package com.bridj to org.bridj
- Vastly improved JNAerator support : now decent and complete :-)
- Enhanced C support
  - Faster structs that rely more on JNAerator
  - Optimized structs performance (added comparison tests with Javolution &amp; JNA)
- Enhanced C++ support :
  - Automatic call of C++ destructors
  - Better GCC demangling (contributed by Remi Emonet)
  - Fixed long and size_t signature matching (@CLong and @Ptr annotations)
  - Fixed size computation of (simply) inherited structs and classes
- Enhanced Pointer&lt;T&gt; class :
  - Full JavaDocs
  - Implements List&lt;T&gt;
  - Support for 2D &amp; 3D arrays, with syntax directly equivalent to C :
      float array[100][200];
      float value = array[i][j];
    Is now :
      Pointer&lt;Pointer<Float&gt;> array = pointerToFloats(new float[width][height]);
      // or
      Pointer&lt;Pointer<Float&gt;> array = pointerToFloats(width, height);
      float value = array.get(i).get(j);
  - More consistent read/write methods, added variants for CLong, SizeT, Boolean, CString, WideCString, PascalString, WidePascalString...
  - Renamed getRemainingElements to getValidElements, getRemainingBytes to getValidBytes 
  - Added standard bounds-checked util methods : 
    - Pointer.copyTo (memcpy)
    - moveTo (memmov)
    - clearBytes (memset)
    - findByte (memchr)
  - Fixed multiple-endianness support for floats and doubles
- Added JAWTUtils.getNativePeerHandle(Component)
- Refactored native libraries (a bit smaller)
- Enhanced runtime :
  - Accept .drv as .dll files on Windows
  - Support for name aliasing (@Name, in addition to the @Symbol annotation that bypasses demangling)
  - Now compiling native libraries with full optimizations
  - Fixed assembler optimizations on Win32, added experimental optimizations for Linux64
  - Can now control assembler optimizations : BridJ.setDirectModeEnabled(boolean)
- Built for Win32/64, MacOS X (Universal: x86, x64, ppc), Ubuntu Linux (x86, x64)

## BridJ 0.1

- Basic support for C (enums, functions, callbacks, structs with native getters/setters)
- Basic support for C++ with annotations- and demangling-based dynamic signature matching :
  - Normal and virtual methods
  - No class fields
  - No templates
  - No destructors
  - No non-trivial constructors)
- Built for Win32/64, MacOS X (Universal: x86, x64, ppc), Linux32
