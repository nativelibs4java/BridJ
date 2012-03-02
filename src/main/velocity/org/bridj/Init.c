#set ($replacedSubPackage = $versionSpecificSubPackage.replaceAll("_", "_1"))
JNIEXPORT void JNICALL Java_org_bridj_${replacedSubPackage}_Platform_init(JNIEnv *env, jclass clazz)
{
	DLLib* pLib = dlLoadLibrary(NULL);
	DLSyms* pSyms = dlSymsInit(NULL);
	int nSyms = dlSymsCount(pSyms);
	
	jclass objectClass = (*env)->FindClass(env, "java/lang/Object");
	jclass signatureHelperClass = (*env)->FindClass(env, "org/bridj/$versionSpecificSubPackage/util/JNIUtils");
	jmethodID decodeMethodNameClassAndSignatureMethod = (*env)->GetStaticMethodID(env, signatureHelperClass, "decodeMethodNameClassAndSignature", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"); 
	
	JNINativeMethod meth;
	memset(&meth, 0, sizeof(JNINativeMethod));
	
	char* tmp = NULL;
	int tmpLen = 0;
	
	const char* packagePattern = "Java_org_bridj_";
	int packagePatternLen = strlen(packagePattern);
	const char* subPackage = "${replacedSubPackage}_";
	int subPackageLen = strlen(subPackage);
	
	jobjectArray nameAndSigArray = (*env)->NewObjectArray(env, 2, objectClass, NULL);
	
	printf("INFO: Found %d symbols\n", nSyms);
	
	for (int iSym = 0; iSym < nSyms; iSym++) {
		const char* symbolName = dlSymsName(pSyms, iSym);
		const char* occ = strstr(symbolName, packagePattern);
		if (occ) {
			size_t symbolNameLen = strlen(symbolName);
			if (tmpLen < symbolNameLen) {
				if (tmp)
					free(tmp);
				tmpLen = symbolNameLen < 100 ? symbolNameLen : 100;
				tmp = malloc(tmpLen + 1);
			}
			int actualPackageLen = (occ - symbolName) + packagePatternLen;
			char* out = tmp;
			
			memcpy(out, symbolName, actualPackageLen);
			out += actualPackageLen;
			
			memcpy(out, subPackage, subPackageLen);
			out += subPackageLen;
			
			strcpy(out, occ + packagePatternLen);
			
			if (meth.fnPtr = dlFindSymbol(pLib, tmp)) {
				jstring declaringClassName = (*env)->CallStaticObjectMethod(env, signatureHelperClass, decodeMethodNameClassAndSignatureMethod, (*env)->NewStringUTF(env, symbolName), nameAndSigArray);
				
				if ((*env)->ExceptionCheck(env))
					goto version_specific_init_failed;
				
				if (declaringClassName) {
					jstring methodName = (*env)->GetObjectArrayElement(env, nameAndSigArray, 1);
					jstring methodSignature = (*env)->GetObjectArrayElement(env, nameAndSigArray, 0);
					const char* declaringClassNameStr = (char*)GET_CHARS(declaringClassName);
					jclass declaringClass = (*env)->FindClass(env, declaringClassNameStr);
					meth.name = (char*)GET_CHARS(methodName);
					meth.signature = (char*)GET_CHARS(methodSignature);
					
					printf("INFO: Registering %s.%s with signature %s as %s\n", declaringClassNameStr, methodName, methodSignature, tmp);
					(*env)->RegisterNatives(env, declaringClass, &meth, 1);
					
					RELEASE_CHARS(methodName, meth.name);
					RELEASE_CHARS(methodSignature, meth.signature);
					RELEASE_CHARS(declaringClassName, declaringClassNameStr);
				}
			} else {
				printf("ERROR: Could not find symbol %s\n", tmp); 
			}
		}
	}
	dlSymsCleanup(pSyms);
	//dlFreeLibrary(pLib); // TODO ?
	
	Java_org_bridj_Platform_init(env, clazz);
	
version_specific_init_failed:
	if (tmp)
		free(tmp);
	
}
