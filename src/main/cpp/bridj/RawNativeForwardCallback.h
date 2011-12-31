#ifndef RawNativeForwardCallback_H
#define RawNativeForwardCallback_H

#include "../dyncallback/dyncall_callback.h"

#ifdef __cplusplus
extern "C" {
#endif 

typedef struct DCAdapterCallback DCAdapterCallback;

#ifdef _WIN32
__declspec(dllexport)
#else
DC_API
#endif
DCAdapterCallback* dcRawCallAdapterSkipTwoArgs(void (*handler)(), int callMode);

#ifdef __cplusplus
}
#endif 

#endif // DYNCALL_CALLBACK_H

