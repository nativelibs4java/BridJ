#set ($replacedSubPackage = $versionSpecificSubPackage.replaceAll("_", "_1"))

\#include "../autovar/autovar_OS.h"
\#include "../autovar/autovar_OSFAMILY.h"

\#if defined(OS_Darwin)
\#include <dlfcn.h>

JNIEXPORT void JNICALL Java_org_bridj_${replacedSubPackage}_Platform_init(JNIEnv *env, jclass clazz);

const char* getBridJLibPath()
{
	const char* libPath;
	Dl_info info;
	dladdr(Java_org_bridj_${replacedSubPackage}_Platform_init, &info);
	libPath = info.dli_fname;
	printf("INFO: BridJ library = '%s'\n", libPath);
	return libPath;
}
\#else
const char* getBridJLibPath()
{
	printf("WARN: NOT DARWIN\n");
	return NULL;
}
#endif

\#if defined(OSFAMILY_Unix)
\#include <dlfcn.h>

void* getSelfSymbol(DLLib* pLib, const char* name) {
	void *sym, *handle;
\#if defined(OS_Darwin)
	handle = RTLD_SELF;
\#else
	handle = RTLD_DEFAULT;
#endif
	
	sym = dlsym(handle, name);
	if (!sym && *name == '_')
		sym = dlsym(handle, name + 1);
	
	return sym;
}
\#else
void* getSelfSymbol(DLLib* pLib, const char* name) {
	return dlFindSymbol(pLib, name);
}
#endif

JNIEXPORT void JNICALL Java_org_bridj_${replacedSubPackage}_Platform_init(JNIEnv *env, jclass clazz)
{
	const char* libPath = getBridJLibPath();
	DLLib* pLib = dlLoadLibrary(libPath);
	DLSyms* pSyms = dlSymsInit(libPath);
	int nSyms = dlSymsCount(pSyms);
	
	jclass objectClass = (*env)->FindClass(env, "java/lang/Object");
	jclass signatureHelperClass = (*env)->FindClass(env, "org/bridj/$versionSpecificSubPackage/util/JNIUtils");
	jmethodID decodeVersionSpecificMethodNameClassAndSignatureMethod = (*env)->GetStaticMethodID(env, signatureHelperClass, "decodeVersionSpecificMethodNameClassAndSignature", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"); 
	
	JNINativeMethod meth;
	memset(&meth, 0, sizeof(JNINativeMethod));
	
	const char* packagePattern = "Java_org_bridj_";
	int packagePatternLen = strlen(packagePattern);
	
	jobjectArray nameAndSigArray = (*env)->NewObjectArray(env, 2, objectClass, NULL);
	
	//printf("INFO: Found %d symbols\n", nSyms);
	for (int iSym = 0; iSym < nSyms; iSym++) {
		const char* symbolName = dlSymsName(pSyms, iSym);
		if (!strcmp(*symbolName == '_' ? symbolName + 1 : symbolName, "Java_org_bridj_${replacedSubPackage}_Platform_init"))
			continue;
		
		if (strstr(symbolName, packagePattern)) {
			if (meth.fnPtr = getSelfSymbol(pLib, symbolName)) {
				jstring declaringClassName = (*env)->CallStaticObjectMethod(env, signatureHelperClass, decodeVersionSpecificMethodNameClassAndSignatureMethod, (*env)->NewStringUTF(env, symbolName), nameAndSigArray);
				
				if ((*env)->ExceptionCheck(env)) {
					printf("ERROR: Exception when trying to find method for symbol '%s'\n", symbolName);
					goto version_specific_init_failed;
				}
				
				if (declaringClassName) {
					jstring methodName = (*env)->GetObjectArrayElement(env, nameAndSigArray, 0);
					jstring methodSignature = (*env)->GetObjectArrayElement(env, nameAndSigArray, 1);
					const char* declaringClassNameStr = (char*)GET_CHARS(declaringClassName);
					jclass declaringClass = (*env)->FindClass(env, declaringClassNameStr);
					meth.name = (char*)GET_CHARS(methodName);
					meth.signature = (char*)GET_CHARS(methodSignature);
					
					//printf("INFO: Registering %s.%s with signature %s as %s\n", declaringClassNameStr, meth.name, meth.signature, symbolName);
					(*env)->RegisterNatives(env, declaringClass, &meth, 1);
					
					RELEASE_CHARS(methodName, meth.name);
					RELEASE_CHARS(methodSignature, meth.signature);
					RELEASE_CHARS(declaringClassName, declaringClassNameStr);
				} else {
					printf("ERROR: Failed to find method for symbol '%s'\n", symbolName);
				}
			} else {
				printf("ERROR: Could not find symbol %s\n", symbolName); 
			}
		}
	}
	
	Java_org_bridj_Platform_init(env, clazz);
	
version_specific_init_failed:
	//printf("INFO: Finished binding of symbols\n");
				
	//dlFreeLibrary(pLib); // TODO ?
	dlSymsCleanup(pSyms);
}
