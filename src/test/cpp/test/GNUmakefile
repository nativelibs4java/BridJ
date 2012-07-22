#BUILD_CONFIG=debug

TOP =	$(DYNCALL_HOME)/dyncall
include $(DYNCALL_HOME)/dyncall/buildsys/gmake/prolog.gmake

TARGET_DLL	 = test
INSTALL_LIB	 = 1

# uses some dyncall headers - no other dependencies.

UNITS += test 
UNITS += structs 
UNITS += templates
ifdef BUILD_OS_darwin
UNITS += objctest
endif

#CPPFLAGS	+= -fvisibility=hidden -fvisibility-inlines-hidden 
#LDFLAGS		+= -fvisibility=hidden -fvisibility-inlines-hidden 

ifdef BUILD_OS_darwin
CPPFLAGS	+=	-I/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers/
#LDFLAGS		+= -lobjc
LDFLAGS		+= -framework Foundation
endif

ifdef BUILD_OS_iphoneos
CPPFLAGS		+= -I/Users/ochafik/src/jamvm/src
LDFLAGS		+= -lobjc
endif

ifdef BUILD_OS_linux
CPPFLAGS	+=	-I/usr/lib/jvm/java-6-sun/include/
CPPFLAGS	+=	-I/usr/lib/jvm/java-6-sun/include/linux
CPPFLAGS	+=	-I/usr/lib/jvm/java-6-openjdk/include/
CPPFLAGS	+=	-I/usr/lib/jvm/java-6-openjdk/include/linux
#LDFLAGS		+= -ljni
endif

ifdef BUILD_OS_sunos
# http://gcc.gnu.org/onlinedocs/gcc/Solaris-2-Options.html
CPPFLAGS	+=	-I/usr/jdk/instances/jdk1.5.0/include
CPPFLAGS	+=	-I/usr/jdk/instances/jdk1.5.0/include/solaris
LDFLAGS		+=	-mimpure-text
LDFLAGS		+=	-L/opt/csw/gcc4/lib
endif                 

ifdef BUILD_OS_windows
CPPFLAGS	+=	"-IC:\Program Files\Microsoft SDKs\Windows\v6.0A\Include"
endif

LINK_LIBS	+= stdc++

include $(DYNCALL_HOME)/dyncall/buildsys/gmake/epilog.gmake
