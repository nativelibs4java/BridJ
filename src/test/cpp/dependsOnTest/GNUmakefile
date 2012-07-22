#BUILD_CONFIG=debug

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

LDFLAGS		+= -rpath 

include $(DYNCALL_HOME)/dyncall/buildsys/gmake/epilog.gmake
