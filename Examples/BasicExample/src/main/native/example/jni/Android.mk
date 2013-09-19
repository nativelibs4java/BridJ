LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := example
NDK_DEBUG := 1

LOCAL_LDFLAGS += -Wl,--export-dynamic -Wl,--allow-multiple-definition
LOCAL_CFLAGS += -U_FORTIFY_SOURCE -std=c99

# For thumb, call with LOCAL_ARM_MODE=thumb
LOCAL_ARM_MODE := $(if $(LOCAL_ARM_MODE),$(LOCAL_ARM_MODE),arm)

EXAMPLE_DIR := $(LOCAL_PATH)/src/main/native/example
LOCAL_C_INCLUDES += $(EXAMPLE_DIR)/include

$(MYLIB_INCLUDES_PATH) 

EXAMPLE_FILES += $(wildcard $(EXAMPLE_DIR)/*.c)
EXAMPLE_FILES += $(wildcard $(EXAMPLE_DIR)/*.cpp)

LOCAL_SRC_FILES += $(EXAMPLE_FILES:$(LOCAL_PATH)/%=%) 

include $(BUILD_SHARED_LIBRARY)


