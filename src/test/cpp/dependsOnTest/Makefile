# Requires the following variables:
# OS, ARCH, CONFIG, DYNCALL_HOME, OBJ_DIR, OUT_DIR

SHARED_LIB	 = dependsOnTest

UNITS += dependsOnTest

CPPFLAGS	+=	-I../test
CPPFLAGS	+= -fPIC

ifeq ($(OS), darwin)
CPPFLAGS  += -mmacosx-version-min=10.5
LDFLAGS   += -mmacosx-version-min=10.5
LDFLAGS		+= -rpath
else
ifeq ($(OS), sunos)
# http://gcc.gnu.org/onlinedocs/gcc/Solaris-2-Options.html
LDFLAGS		+= -mimpure-text
else
LDFLAGS		+= -Wl,-rpath '-Wl,$$ORIGIN' -Wl,--enable-new-dtags
endif
endif

ifeq ($(OS), windows)
CPPFLAGS	+=	"-IC:\Program Files\Microsoft SDKs\Windows\v6.0A\Include"
endif

#LDFLAGS   += -l stdc++
LDFLAGS   += -L $(OBJ_DIR) -L $(OUT_DIR)
LDFLAGS	  += -l test

include ../../../main/cpp/bridj/epilog.make
