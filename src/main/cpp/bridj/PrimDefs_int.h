#include "PrimDefs_undef.h"
#define primName 		int
#define jprimName 		jint
#define jprimArray 		jintArray
#define primJNICapName 	Int
#define primCapName 	Int
#define wrapperName 	Integer
#define bufferName 		IntBuffer
#define primSize		4
#define alignmentMask	3
#define TEMP_REORDER_VAR_TYPE jint

//#ifdef BIG_ENDIAN
//#define REORDER_VALUE_BYTES(peer) REORDER_VALUE_BYTES_jint(peer, 0, 1, 2, 3)
//#else
#define REORDER_VALUE_BYTES(peer) REORDER_VALUE_BYTES_jint(peer, 3, 2, 1, 0)
//#endif
