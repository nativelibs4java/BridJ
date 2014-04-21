# Requires the following variables:
# OS, ARCH, CONFIG, DYNCALL_HOME, OBJ_DIR, OUT_DIR, DYNCALL_BUILD_DIR

SHARED_LIB    = bridj

UNITS += CallbackHandler
UNITS += CppHandler
UNITS += Errors
UNITS += Exceptions
UNITS += FunctionHandler
UNITS += GenericJavaCallbackHandlers
UNITS += HandlersCommon
UNITS += JNI
ifeq ($(OS), darwin)
UNITS += ObjCBlocks
UNITS += ObjCHandler
UNITS += ObjCProxy
endif
UNITS += Platform
UNITS += RawNativeForwardCallback
UNITS += RawNativeForwardCallback_arch
UNITS += ThreadLocal
UNITS += Utils

CPPFLAGS  += -I$(DYNCALL_HOME)/dyncall
CPPFLAGS  += -I$(DYNCALL_HOME)/dyncall/dyncall
CPPFLAGS  += -I$(DYNCALL_HOME)/dyncall/dyncallback
CPPFLAGS  += -I$(DYNCALL_HOME)/dyncall/dynload

ifneq ($(ARCH), armhf)
CPPFLAGS  += -mtune=generic
endif

ifeq ($(OS), darwin)
CPPFLAGS  += -no-integrated-as
CPPFLAGS  += -D_DARWIN_C_SOURCE
CPPFLAGS  += -I/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers/
CPPFLAGS  += -mmacosx-version-min=10.4
LDFLAGS   += -mmacosx-version-min=10.4
LDFLAGS	  += -ldl -framework Foundation -all_load
else
ifneq ($(OS), sunos)
LDFLAGS   += -Wl,--hash-style=both
endif
endif

# Lower requirement for GLIBC version number (2.2.5) :
# objdump -p src/main/resources/org/bridj/lib/linux_x64/bridj.so | fgrep 'Version References:' -A 100
CPPFLAGS  += -U_FORTIFY_SOURCE
#CPPFLAGS += -Werror
CPPFLAGS  += -std=c99

ifeq ($(ARCH), x64)
UNITS += GLIBCCompatibilityWraps
CPPFLAGS  += -Wl,--wrap=memcpy
endif

CPPFLAGS  += -fno-stack-protector

LDFLAGS		+= -lpthread
CPPFLAGS	+= -fPIC

ifeq ($(OS), sunos)
# http://gcc.gnu.org/onlinedocs/gcc/Solaris-2-Options.html
CPPFLAGS  += -funsafe-loop-optimizations
CPPFLAGS	+=	-D__EXTENSIONS__
LDFLAGS		+= 	-mimpure-text
LDFLAGS		+=	-L/opt/csw/gcc4/lib
# Statically link against libgcc.a on Solaris, to avoid dependency to libgcc_s.so.x.x
# See https://github.com/ochafik/nativelibs4java/issues/452
LDFLAGS   += -static-libgcc
endif

ifeq ($(OS), linux)

# GNU_SOURCE needs to be defined for dlopen, and undefined for strerror_r.
# CPPFLAGS  +=  -D_GNU_SOURCE
CPPFLAGS += -z noexecstack
endif

CPPFLAGS  +=  -I$(JAVA_HOME)/include/
CPPFLAGS  +=  -I$(JAVA_HOME)/include/linux
CPPFLAGS  +=  -I$(JAVA_HOME)/include/solaris
CPPFLAGS  +=  -I$(JAVA_HOME)/include/windows
CPPFLAGS  +=  -I$(JAVA_HOME)/include/darwin

LDFLAGS   += -L$(DYNCALL_HOME)/dyncall/dyncall/$(DYNCALL_BUILD_DIR)
LDFLAGS   += -L$(DYNCALL_HOME)/dyncall/dynload/$(DYNCALL_BUILD_DIR)
LDFLAGS   += -L$(DYNCALL_HOME)/dyncall/dyncallback/$(DYNCALL_BUILD_DIR)
LDFLAGS   += -L$(DYNCALL_HOME)/dyncall/dyncall
LDFLAGS   += -L$(DYNCALL_HOME)/dyncall/dynload
LDFLAGS   += -L$(DYNCALL_HOME)/dyncall/dyncallback
LDFLAGS   += -l dyncall_s -l dynload_s -l dyncallback_s

include epilog.make
