#include "PrimDefs_undef.h"
#define primName 		char
#define jprimName 		jchar
#define jprimArray 		jcharArray
#define primJNICapName 	Char
#define primCapName 	Char
#define wrapperName 	Character
#define bufferName 		CharBuffer
#define primSize		2
#define alignmentMask	1
#define TEMP_REORDER_VAR_TYPE jshort

//#ifdef BIG_ENDIAN
//#define REORDER_VALUE_BYTES(peer) REORDER_VALUE_BYTES_jshort(peer, 0, 1)
//#else
#define REORDER_VALUE_BYTES(peer) REORDER_VALUE_BYTES_jshort(peer, 1, 0)
//#endif
