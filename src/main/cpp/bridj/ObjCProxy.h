#pragma once
#ifndef _OBJC_PROXY_H
#define _OBJC_PROXY_H

#include <jni.h>

#import <Foundation/Foundation.h>

@interface ObjCProxy : NSObject 

	{
		jobject javaInstance;
	}

	- (id) initWithEnv: (JNIEnv*)env javaInstance: (jobject)theJavaInstance;
	
	- dealloc;
	
	- (void)forwardInvocation:(NSInvocation *) anInvocation;
	
	- (NSMethodSignature *)methodSignatureForSelector:(SEL)aSelector;

@end

id newObjCProxy(JNIEnv* env, jobject javaInstance);

#endif // _OBJC_PROXY_H
