LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)

LOCAL_MODULE    := example

#APP_ABI := $(if $(APP_ABI),$(APP_ABI),armeabi)
#APP_ABI := armeabi x86 mips
APP_ABI += x86 
APP_OPTIM := release

# Add shared library dependencies here (libmyDependency.so):
# LOCAL_LDFLAGS += -lmyDependency
LOCAL_LDLIBS := -llog

LOCAL_LDFLAGS += -Wl,--export-dynamic -Wl,--allow-multiple-definition
LOCAL_CFLAGS += -U_FORTIFY_SOURCE
LOCAL_ARM_MODE := $(if $(LOCAL_ARM_MODE),$(LOCAL_ARM_MODE),arm)

# Add include paths here:
# LOCAL_C_INCLUDES += ...

$(MYLIB_INCLUDES_PATH) 

SRC_FILES += $(wildcard $(LOCAL_PATH)/*.c)
SRC_FILES += $(wildcard $(LOCAL_PATH)/*.cpp)
SRC_FILES += $(wildcard $(LOCAL_PATH)/*.cxx)
  
LOCAL_SRC_FILES += $(SRC_FILES:$(LOCAL_PATH)/%=%) 

include $(BUILD_SHARED_LIBRARY)
