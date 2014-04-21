#include "autovar/autovar_ARCH.h"
#include <string.h>

#ifdef ARCH_X64

#define WRAP_SUB(name, return_keyword, return_type, arg_defs, args, underscored_version, dotted_version) \
  return_type __ ## name ## _ ## underscored_version arg_defs; \
  __asm__(".symver __" #name "_" #underscored_version ", " #name "@" #dotted_version); \
  return_type __wrap_ ## name arg_defs { \
    return_keyword __ ## name ## _ ## underscored_version args; \
  }

#define WRAP(name, return_keyword, return_type, arg_defs, args) \
  WRAP_SUB(name, return_keyword, return_type, arg_defs, args, \
    glibc_2_2_5, GLIBC_2.2.5)

WRAP(memcpy, return, void*, (void *dest, const void *src, size_t n), (dest, src, n));

#endif
