#//////////////////////////////////////////////////////////////////////////////
#
# Copyright (c) 2007,2009 Daniel Adler <dadler@uni-goettingen.de>, 
#                         Tassilo Philipp <tphilipp@potion-studios.com>
#
# Permission to use, copy, modify, and distribute this software for any
# purpose with or without fee is hereby granted, provided that the above
# copyright notice and this permission notice appear in all copies.
#
# THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
# WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
# MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
# ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
# WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
# ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
# OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
#
#//////////////////////////////////////////////////////////////////////////////

#BUILD_CONFIG=debug

DLL_PREFIX=lib
LD=gcc
CXX=gcc

COMPILE_PIC = 1

TOP =	$(DYNCALL_HOME)/dyncall
include $(DYNCALL_HOME)/dyncall/buildsys/gmake/prolog.gmake

TARGET_DLL	 = bridj
INSTALL_LIB	 = 1

# uses some dyncall headers - no other dependencies.

CPPFLAGS	 += -I$(DYNCALL_HOME)/dyncall
CPPFLAGS	 += -I$(DYNCALL_HOME)/dyncall/dyncall
CPPFLAGS	 += -I$(DYNCALL_HOME)/dyncall/dyncallback
CPPFLAGS	 += -I$(DYNCALL_HOME)/dyncall/dynload

UNITS += Exceptions
UNITS += JNI
UNITS += Platform
UNITS += Pointer

UNITS += HandlersCommon 
UNITS += GenericJavaCallbackHandlers
UNITS += CallbackHandler
UNITS += CppHandler
UNITS += FunctionHandler
#UNITS += StructHandler
UNITS += CallbackHandler
UNITS += ThreadLocal
ifdef BUILD_OS_darwin
UNITS += ObjCHandler ObjCProxy ObjCBlocks
endif
ifdef BUILD_OS_iphoneos
UNITS += ObjCHandler ObjCProxy ObjCBlocks
endif
UNITS += RawNativeForwardCallback
#ifdef BUILD_TOOL_gcc
UNITS += RawNativeForwardCallback_arch
#endif

ifndef BUILD_OS_darwin
CPPFLAGS += -Werror
CPPFLAGS += -D_DARWIN_C_SOURCE
endif

CPPFLAGS += -std=c99
#CFLAGS += -msse2 -msseregparm
#CFLAGS += -g

# Lower requirement for GLIBC version number (2.2.5) :
# objdump -p src/main/resources/org/bridj/lib/linux_x64/bridj.so | fgrep 'Version References:' -A 100
CPPFLAGS += -U_FORTIFY_SOURCE

ifndef BUILD_OS_sunos
CPPFLAGS += -fno-stack-protector -funsafe-loop-optimizations
endif

#LDFLAGS += -Wl,--as-needed
#LDFLAGS += -static-libgcc -Wl,-Bstatic -libc

ifdef BUILD_OS_darwin
CPPFLAGS		+= -I/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers/
CPPFLAGS 	+= -DBRIDJ_OBJC_SUPPORT
LDFLAGS		+= -ldl -framework Foundation -all_load 
endif

ifdef BUILD_OS_iphoneos
CPPFLAGS		+= -I/Users/ochafik/src/jamvm/src
CPPFLAGS 	+= -DBRIDJ_OBJC_SUPPORT
LDFLAGS		+= -ldl -framework Foundation -all_load 
endif

ifndef BUILD_OS_android
ifndef BUILD_ARCH_arm
LDFLAGS		+= -lpthread
endif
endif

ifdef BUILD_OS_linux
CPPFLAGS	+=	-D_GNU_SOURCE
CPPFLAGS	+=	-I/usr/lib/jvm/java-6-sun/include/
CPPFLAGS	+=	-I/usr/lib/jvm/java-6-sun/include/linux
CPPFLAGS	+=	-I/usr/lib/jvm/java-6-openjdk/include/
CPPFLAGS	+=	-I/usr/lib/jvm/java-6-openjdk/include/linux
endif

ifdef BUILD_OS_sunos
# http://gcc.gnu.org/onlinedocs/gcc/Solaris-2-Options.html
CPPFLAGS	+=	-D__EXTENSIONS__
CPPFLAGS	+=	-I/usr/jdk/instances/jdk1.5.0/include
CPPFLAGS	+=	-I/usr/jdk/instances/jdk1.5.0/include/solaris
LDFLAGS		+= 	-mimpure-text
LDFLAGS		+=	-L/opt/csw/gcc4/lib
endif                 

ifdef BUILD_OS_darwin
CPPFLAGS += -mmacosx-version-min=10.4
LDFLAGS += -mmacosx-version-min=10.4
endif

ifdef BUILD_CONFIG_debug
CPPFLAGS +=	-O0 -g
else
CPPFLAGS += -O3
endif

#LINK_LIBS	+= stdc++

#CPPFLAGS += -DJNICALL=__cdecl -DJNIEXPORT=__cdecl

#CPPFLAGS	+= -fvisibility=hidden -fvisibility-inlines-hidden 
#LDFLAGS		+= -fvisibility=hidden -fvisibility-inlines-hidden 

LINK_DIRS	+= $(DYNCALL_HOME)/dyncall/dyncall/$(BUILD_DIR) 
LINK_DIRS	+= $(DYNCALL_HOME)/dyncall/dynload/$(BUILD_DIR) 
LINK_DIRS	+= $(DYNCALL_HOME)/dyncall/dyncallback/$(BUILD_DIR) 
LINK_LIBS   += dyncall_s dynload_s dyncallback_s

include $(DYNCALL_HOME)/dyncall/buildsys/gmake/epilog.gmake
