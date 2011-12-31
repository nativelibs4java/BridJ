#include "dyncall_macros.h"

#if defined(DC__OS_Darwin)
#	if defined(DC__Arch_Intel_x86)
		//TODO
#	elif defined(DC__Arch_AMD64)
#    		include "RawNativeForwardCallback_Darwin64.s"
#	endif
#elif defined(DC__OS_Linux)
#	if defined(DC__Arch_Intel_x86)
		//TODO
#	elif defined(DC__Arch_AMD64)
#		include "RawNativeForwardCallback_Linux64.s"
#	elif defined(DC__Arch_PowerPC)

#	endif
#else
#	if defined(DC__Arch_Intel_x86)
		//TODO
#	elif defined(DC__Arch_AMD64)
		//TODO
#	endif
#endif

