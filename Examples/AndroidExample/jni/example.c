#include "example.h"
#include <string.h>  
#include <android/log.h>  

#define DEBUG_TAG "NDK_BridJExample"

void helloLog(const char *szLogThis)
{
    __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: [%s]", szLogThis);
}
