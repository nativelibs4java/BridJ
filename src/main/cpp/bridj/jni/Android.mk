LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)

LOCAL_MODULE    := bridj
APP_ABI := $(if $(APP_ABI),$(APP_ABI),armeabi)
APP_OPTIM := release
LOCAL_LDFLAGS += -ldl -Wl,--export-dynamic
LOCAL_CFLAGS += -U_FORTIFY_SOURCE

# For thumb, call with LOCAL_ARM_MODE=thumb
LOCAL_ARM_MODE := $(if $(LOCAL_ARM_MODE),$(LOCAL_ARM_MODE),arm)

DYNCALL_DIR := $(LOCAL_PATH)/dyncall/dyncall
DYNCALLBACK_DIR := $(LOCAL_PATH)/dyncall/dyncallback
DYNLOAD_DIR := $(LOCAL_PATH)/dyncall/dynload

LOCAL_C_INCLUDES += $(LOCAL_PATH)/dyncall
LOCAL_C_INCLUDES += $(DYNCALL_DIR)
LOCAL_C_INCLUDES += $(DYNCALLBACK_DIR)
LOCAL_C_INCLUDES += $(DYNLOAD_DIR)

$(MYLIB_INCLUDES_PATH) 

DYNLOAD_FILES += $(DYNLOAD_DIR)/dynload.c
DYNLOAD_FILES += $(DYNLOAD_DIR)/dynload_syms.c
#DYNLOAD_FILES += $(DYNLOAD_DIR)/dynload_syms_elf.c
LOCAL_SRC_FILES += $(DYNLOAD_FILES:$(LOCAL_PATH)/%=%) 

DYNCALL_FILES += $(DYNCALL_DIR)/dyncall_vector.c
DYNCALL_FILES += $(DYNCALL_DIR)/dyncall_api.c
DYNCALL_FILES += $(DYNCALL_DIR)/dyncall_callvm.c
DYNCALL_FILES += $(DYNCALL_DIR)/dyncall_callvm_base.c
DYNCALL_FILES += $(DYNCALL_DIR)/dyncall_struct.c

ifeq ($(TARGET_ARCH_ABI),armeabi) 
ifeq ($(LOCAL_ARM_MODE),arm)
DYNCALL_FILES += $(DYNCALL_DIR)/dyncall_call_arm32_arm.S
else
DYNCALL_FILES += $(DYNCALL_DIR)/dyncall_call_arm32_thumb_gas.s
endif
else
DYNCALL_FILES += $(DYNCALL_DIR)/dyncall_call_x86.S
endif
LOCAL_SRC_FILES += $(DYNCALL_FILES:$(LOCAL_PATH)/%=%) 

DYNCALLBACK_FILES += $(DYNCALLBACK_DIR)/dyncall_thunk.c
DYNCALLBACK_FILES += $(DYNCALLBACK_DIR)/dyncall_alloc_wx.c
DYNCALLBACK_FILES += $(DYNCALLBACK_DIR)/dyncall_args.c
DYNCALLBACK_FILES += $(DYNCALLBACK_DIR)/dyncall_callback.c
DYNCALLBACK_FILES += $(DYNCALLBACK_DIR)/dyncall_callback_arch.S
LOCAL_SRC_FILES += $(DYNCALLBACK_FILES:$(LOCAL_PATH)/%=%) 

BRIDJ_DIR := $(LOCAL_PATH)/..
BRIDJ_FILES := $(wildcard $(BRIDJ_DIR)/*.c)
LOCAL_SRC_FILES += $(BRIDJ_FILES:$(LOCAL_PATH)/%=%) 

include $(BUILD_SHARED_LIBRARY)


