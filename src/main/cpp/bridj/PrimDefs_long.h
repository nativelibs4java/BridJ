#include "PrimDefs_undef.h"
#define primName 		long
#define jprimName 		jlong
#define jprimArray 		jlongArray
#define primJNICapName 	Long
#define primCapName 	Long
#define wrapperName 	Long
#define bufferName 		LongBuffer
#define primSize		8
#define alignmentMask	7
#define TEMP_REORDER_VAR_TYPE jlong

//#ifdef BIG_ENDIAN
//#define REORDER_VALUE_BYTES(peer) REORDER_VALUE_BYTES_jlong(peer, 0, 1, 2, 3, 4, 5, 6, 7)
//#else
#define REORDER_VALUE_BYTES(peer) REORDER_VALUE_BYTES_jlong(peer, 7, 6, 5, 4, 3, 2, 1, 0)
//#endif
