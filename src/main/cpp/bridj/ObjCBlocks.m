#include "HandlersCommon.h"

#ifdef BRIDJ_OBJC_SUPPORT
#include <objc/objc.h>
#include <Block.h>
#include "bridj.hpp"

/*
http://cocoawithlove.com/2009/10/how-blocks-are-implemented-and.html
http://www.opensource.apple.com/source/libclosure/libclosure-38/BlockImplementation.txt
*/

enum {
    BLOCK_HAS_COPY_DISPOSE =  (1 << 25),
    BLOCK_HAS_CTOR =          (1 << 26),
    BLOCK_IS_GLOBAL =         (1 << 28),
    BLOCK_HAS_DESCRIPTOR =    (1 << 29),
};

typedef struct _block_descriptor {
	unsigned long int reserved;
	unsigned long int block_size;
	void (*copy_helper)(void *dst, void *src);
	void (*dispose_helper)(void *src);
} _block_descriptor;	

typedef struct _block_literal {
	void* isa;
	int flags;
	int reserved; 
	void (*invoke)(void *, ...);
	struct _block_descriptor* descriptor;
} _block_literal;	

const void* createObjCBlock() {
	void (^block)() = ^{
		// do nothing
	};
	return Block_copy(block);
}
jlong Java_org_bridj_objc_ObjCJNI_getObjCBlockFunctionPointer(JNIEnv* env, jclass cl, jlong jblock)
{
	_block_literal* block = (_block_literal*)JLONG_TO_PTR(jblock);
	return PTR_TO_JLONG(block->invoke);
}
jlong Java_org_bridj_objc_ObjCJNI_createObjCBlockWithFunctionPointer(JNIEnv* env, jclass cl, jlong fptr)
{
	_block_literal* block = (_block_literal*)createObjCBlock();
	block->invoke = JLONG_TO_PTR(fptr);
	return PTR_TO_JLONG(block);
}
void releaseObjCBlock(const void* block) {
	Block_release(block);	
}

#endif

