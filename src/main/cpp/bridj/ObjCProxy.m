#include "JNI.h"
#include "org_bridj_JNI.h"
#include "bridj.hpp"

#import "ObjCProxy.h"

jclass gObjCProxyClass = NULL;
jclass gNSInvocationClass = NULL;
jclass gSELClass = NULL;

jmethodID gObjCProxyMethodSignatureForSelector = NULL;
jmethodID gObjCProxyForwardInvocation = NULL;
jobject gNSInvocationCallIO = NULL;
jobject gSELCallIO = NULL;
jobject gObjCProxyCallIO = NULL;

jclass gCallIOUtilsClass = NULL;
jmethodID gCallIOUtilsCreatePointerCallIO = NULL;
jmethodID gCallIOUtilsCreatePointerCallIOToTargetType = NULL;

jobject getCallIOToTargetType(JNIEnv* env, jobject tpe) {
	return (*env)->CallStaticObjectMethod(env, gCallIOUtilsClass, gCallIOUtilsCreatePointerCallIOToTargetType, tpe);
}
jobject getCallIO(JNIEnv* env, jclass cl) {
	return (*env)->CallStaticObjectMethod(env, gCallIOUtilsClass, gCallIOUtilsCreatePointerCallIO, cl);
}
void initObjCProxy(JNIEnv* env) {
	if (gObjCProxyClass)
		return;
	
	gObjCProxyClass = FIND_GLOBAL_CLASS("org/bridj/objc/ObjCProxy");
	gObjCProxyMethodSignatureForSelector = (*env)->GetMethodID(env, 
		gObjCProxyClass, 
		"methodSignatureForSelector", 
		"(Lorg/bridj/objc/SEL;)" POINTER_SIG
	);
	gObjCProxyForwardInvocation = (*env)->GetMethodID(env, 
		gObjCProxyClass, 
		"forwardInvocation", 
		"(" POINTER_SIG ")V" //"(" OBJECT_SIG "Lorg/bridj/objc/SEL;" POINTER_SIG "V"
	);
	gCallIOUtilsClass = FIND_GLOBAL_CLASS("org/bridj/CallIO$Utils");
	gCallIOUtilsCreatePointerCallIO = (*env)->GetStaticMethodID(env,
		gCallIOUtilsClass,
		"createPointerCallIO",
		"(" TYPE_SIG ")" CALLIO_SIG
	);
	gCallIOUtilsCreatePointerCallIOToTargetType = (*env)->GetStaticMethodID(env,
		gCallIOUtilsClass,
		"createPointerCallIOToTargetType",
		"(" TYPE_SIG ")" CALLIO_SIG
	);
	
	gNSInvocationClass = FIND_GLOBAL_CLASS("org/bridj/objc/NSInvocation");
	gSELClass = FIND_GLOBAL_CLASS("org/bridj/objc/SEL");

	gSELCallIO = GLOBAL_REF(getCallIO(env, gSELClass));
	gNSInvocationCallIO = GLOBAL_REF(getCallIOToTargetType(env, gNSInvocationClass));
	gObjCProxyCallIO = GLOBAL_REF(getCallIOToTargetType(env, gObjCProxyClass));
}

@implementation ObjCProxy

- (id) initWithEnv: (JNIEnv*)env javaInstance: (jobject)theJavaInstance {
	self = [super init];
	if (self)
		javaInstance = GLOBAL_REF(theJavaInstance);
	
	return self;
}

- (void)dealloc {
	JNIEnv* env = GetEnv();
	(*env)->DeleteGlobalRef(env, javaInstance);
	[super dealloc];
}

- (void)forwardInvocation: (NSInvocation*)invocation {
	JNIEnv* env = GetEnv();
	jobject ptr = createPointerFromIO(env, invocation, gNSInvocationCallIO);
	(*env)->CallVoidMethod(env, javaInstance, gObjCProxyForwardInvocation, ptr);
}

- (NSMethodSignature*)methodSignatureForSelector: (SEL)sel {
	JNIEnv* env = GetEnv();
	jobject sig = (*env)->CallObjectMethod(env, javaInstance, gObjCProxyMethodSignatureForSelector, createPointerFromIO(env, sel, gSELCallIO));
	if (!sig)
		return [super methodSignatureForSelector: sel];
	return (NSMethodSignature*)getPointerPeer(env, sig);
}

- (BOOL)respondsToSelector: (SEL)sel {
	return [self methodSignatureForSelector: sel] != nil;
}

@end

jobject Java_org_bridj_objc_ObjCJNI_createObjCProxyPeer(JNIEnv* env, jclass cl, jobject javaInstance)
{
	initObjCProxy(env);
	id proxyInstance = [[ObjCProxy alloc] initWithEnv: env javaInstance: javaInstance];
	return createPointerFromIO(env, proxyInstance, gObjCProxyCallIO);
}
