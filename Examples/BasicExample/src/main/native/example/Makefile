# Requires the following variables:
# OS, ARCH, OS_ARCH, CONFIG, OUT_BASE_DIR, OBJ_BASE_DIR

# Name of your shared library.
# Resulting file will have prefix 'lib' and a platform-specific extension.
SHARED_LIB    = example

# List of C / C++ files, without the extension.
UNITS += example
# UNITS += utils

# Include path for the project's own headers.
CPPFLAGS  += -Iinclude
LDFLAGS   += -lstdc++

# Some settings to produce the most generic binaries possible.
CPPFLAGS  += -mtune=generic
CPPFLAGS  += -U_FORTIFY_SOURCE
CPPFLAGS  += -Werror
CPPFLAGS	+= -fPIC
ifeq ($(OS), sunos)
	LDFLAGS		+= 	-mimpure-text
endif
ifeq ($(OS), linux)
	CPPFLAGS  +=  -D_GNU_SOURCE
endif
ifeq ($(OS), darwin)
	CPPFLAGS  += -D_DARWIN_C_SOURCE
	CPPFLAGS  += -mmacosx-version-min=10.4
	LDFLAGS   += -mmacosx-version-min=10.4
	# Uncomment when compiling Objective-C:
	# LDFLAGS	  += -framework Foundation
	SHARED_EXT = dylib
else
	SHARED_EXT = so
endif

ifneq ($(OS), sunos)
	ifneq ($(OS), darwin)
		LDFLAGS   += -Wl,--hash-style=both
	endif
endif

# If your native library includes "jni.h":
# CPPFLAGS  +=  -I$(JAVA_HOME)/include/
# CPPFLAGS  +=  -I$(JAVA_HOME)/include/linux
# CPPFLAGS  +=  -I$(JAVA_HOME)/include/solaris
# CPPFLAGS  +=  -I$(JAVA_HOME)/include/windows

ifeq ($(CONFIG), debug)
	CPPFLAGS  +=	-O0 -g
else
	CPPFLAGS  += -O3
endif

TOP_DIR := ../../..

# Write object files in Maven's output folders.
OBJ_DIR = $(OBJ_BASE_DIR)/native_$(OS_ARCH)_$(CONFIG)_obj
OUT_DIR = $(OUT_BASE_DIR)/$(OS_ARCH)

OBJS += $(foreach X,$(UNITS),$(OBJ_DIR)/$X.o)

TARGET = $(OUT_DIR)/lib$(SHARED_LIB).$(SHARED_EXT)

CC := gcc
C_EXTENSIONS := c m
CC_EXTENSIONS := cc cpp cxx

define cc_rule
$(OBJ_DIR)/%.o : %.$1
	mkdir -p $$(OBJ_DIR)
	$$(CC) -c $2 $$(CPPFLAGS) $$< -o $$@
endef
$(foreach EXT,$(C_EXTENSIONS),$(eval $(call cc_rule,$(EXT),-std=c99)))
$(foreach EXT,$(CC_EXTENSIONS),$(eval $(call cc_rule,$(EXT),)))

$(TARGET): $(OBJS)
	mkdir -p $(OUT_DIR)
	${CC} -shared -o $@ $? $(LDFLAGS) $(CPPFLAGS)

.PHONY: directories
all: directories $(TARGET)
directories: $(OBJ_DIR) $(OUT_DIR)
$(OBJ_DIR):
	mkdir -p $(OBJ_DIR)
$(OUT_DIR):
	mkdir -p $(OBJ_DIR)
clean:
	rm -fR $(OUT_DIR) $(OBJ_DIR) $(TARGET)

