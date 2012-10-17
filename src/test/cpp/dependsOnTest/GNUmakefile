#BUILD_CONFIG=debug

DLL_PREFIX=lib
TOP =	$(DYNCALL_HOME)/dyncall
include $(DYNCALL_HOME)/dyncall/buildsys/gmake/prolog.gmake

TARGET_DLL	 = dependsOnTest
INSTALL_LIB	 = 1

# uses some dyncall headers - no other dependencies.

UNITS += test

CPPFLAGS	+=	-I../test

LINK_LIBS	+= stdc++
LINK_DIRS	+= ../test/$(BUILD_DIR)
LINK_LIBS	+= test

ifdef BUILD_OS_darwin
LDFLAGS		+= -rpath 
else
ifndef BUILD_OS_sunos
LDFLAGS		+= -Wl,-rpath,.
endif
endif

ifdef BUILD_OS_sunos
LDFLAGS		+= -mimpure-text
endif
include $(DYNCALL_HOME)/dyncall/buildsys/gmake/epilog.gmake
