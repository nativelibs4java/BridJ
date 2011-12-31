#include "HandlersCommon.h"

#ifdef BRIDJ_OBJC_SUPPORT
#include <objc/objc.h>
#include <Block.h>
#include "bridj.hpp"

/*
http://cocoawithlove.com/2009/10/how-blocks-are-implemented-and.html
http://www.opensource.apple.com/source/libclosure/libclosure-38/BlockImplementation.txt
*/
typedef struct Block_literal {
	void* isa;
	int flags;
	int reserved; 
	void (*invoke)(void *, ...);
	void* descriptor;
} Block_literal;	
	
const void* createObjCBlock() {
	void (^block)() = ^{
		// do nothing
	};
	return Block_copy(block);
}
jlong Java_org_bridj_objc_ObjCJNI_getObjCBlockFunctionPointer(JNIEnv* env, jclass cl, jlong jblock)
{
	Block_literal* block = (Block_literal*)JLONG_TO_PTR(jblock);
	return PTR_TO_JLONG(block->invoke);
}
jlong Java_org_bridj_objc_ObjCJNI_createObjCBlockWithFunctionPointer(JNIEnv* env, jclass cl, jlong fptr)
{
	Block_literal* block = (Block_literal*)createObjCBlock();
	block->invoke = JLONG_TO_PTR(fptr);
	return PTR_TO_JLONG(block);
}
void releaseObjCBlock(const void* block) {
	Block_release(block);	
}

#endif

