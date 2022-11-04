#include <cstring>
#include <cstdint>
#include <jni.h>
#include <map>
#include <vector>
#include <algorithm>
#include <string>

#include "bridj.hpp"
#include "JNI.h"
#include "dyncall_alloc_wx.h"
	
#if (INTPTR_MAX == 9223372036854775807L)
  #define SIGIL_PTR_VALUE1 (*(size_t*)"TRAMPO1")
  #define SIGIL_PTR_VALUE2 (*(size_t*)"TRAMPO2")
  #define SIGIL_PTR_VALUE3 (*(size_t*)"TRAMPO3")
#else
  #define SIGIL_PTR_VALUE1 (*(size_t*)"TR1")
  #define SIGIL_PTR_VALUE2 (*(size_t*)"TR2")
  #define SIGIL_PTR_VALUE3 (*(size_t*)"TR3")
#endif

bool replaceSizeTValueInBuffer(char *buffer, size_t bufferLength, size_t patternValue, size_t replacementValue) {
  auto found = (size_t*)memmem(buffer, bufferLength, &patternValue, sizeof(decltype(patternValue)));
  if (!found) return false;
  // printf("FOUND VALUE %lu...\n", patternValue);
  *found = replacementValue;
  // printf("WROTE VALUE %lu instead of %lu!\n", replacementValue, patternValue);
  return true;
}

void *copyFunction(void *f, size_t size) {
  if (size < 0) {
    return nullptr;
  }
  void* copy;
	int err = dcAllocWX(size, (void**) &copy);
	if (err != 0) {
		return nullptr;
  }
  memcpy(copy, f, size);
  return copy;
}

#pragma GCC push_options
#pragma GCC optimize ("O0")

// https://en.cppreference.com/w/cpp/language/parameter_pack
template <class RetType, class... ArgTypes>
RetType jniNativeTrampolineTemplate(JNIEnv* env, jobject obj, ArgTypes... args) {
	// decltype RetType (*FnType)(ArgTypes...);
  printf("Inside trampoline!\n");
  auto fptr = (RetType (*)(ArgTypes...)) (size_t) SIGIL_PTR_VALUE1;
  printf("Inside trampoline function, yay; fptr = %lu!!!\n", (size_t) fptr);
  auto x = 10.0;
  return *(RetType*)(void*)&x;
	// return fptr(args...);
}

#pragma GCC pop_options

// size_t sizeOfFunction(void *f) {
//   return 1024;
// }

// int (*A<int>::pfunction)() = &B::getT<int>;

// void* newJNINativeTrampoline(const char* signature, void *f) {

extern "C" {

void *newJNINativeTrampoline(const char* signature, void* fptr) {
	static std::map<std::string, void*> templates;
  static size_t maxFnSize = 0;
	if (templates.empty()) {
    // auto x = (void*)&jniNativeTrampolineTemplate<bool>;
		templates[")v"] = (void*)&jniNativeTrampolineTemplate<void>;
		templates[")B"] = (void*)&jniNativeTrampolineTemplate<bool>;
		templates[")i"] = (void*)&jniNativeTrampolineTemplate<int32_t>;
		templates[")l"] = (void*)&jniNativeTrampolineTemplate<int64_t>;

		templates["i)v"] = (void*)&jniNativeTrampolineTemplate<void, int32_t>;
		templates["i)B"] = (void*)&jniNativeTrampolineTemplate<bool, int32_t>;
		templates["i)i"] = (void*)&jniNativeTrampolineTemplate<int32_t, int32_t>;
		templates["i)l"] = (void*)&jniNativeTrampolineTemplate<int64_t, int32_t>;

		templates["l)v"] = (void*)&jniNativeTrampolineTemplate<void, int64_t>;
		templates["l)B"] = (void*)&jniNativeTrampolineTemplate<bool, int64_t>;
		templates["l)i"] = (void*)&jniNativeTrampolineTemplate<int32_t, int64_t>;
		templates["l)l"] = (void*)&jniNativeTrampolineTemplate<int64_t, int64_t>;

		templates["ll)v"] = (void*)&jniNativeTrampolineTemplate<void, int64_t, int64_t>;
		templates["ll)B"] = (void*)&jniNativeTrampolineTemplate<bool, int64_t, int64_t>;
		templates["ll)i"] = (void*)&jniNativeTrampolineTemplate<int32_t, int64_t, int64_t>;
		templates["ll)l"] = (void*)&jniNativeTrampolineTemplate<int64_t, int64_t, int64_t>;

		templates["lll)v"] = (void*)&jniNativeTrampolineTemplate<void, int64_t, int64_t, int64_t>;
		templates["lll)B"] = (void*)&jniNativeTrampolineTemplate<bool, int64_t, int64_t, int64_t>;
		templates["lll)i"] = (void*)&jniNativeTrampolineTemplate<int32_t, int64_t, int64_t, int64_t>;
		templates["lll)l"] = (void*)&jniNativeTrampolineTemplate<int64_t, int64_t, int64_t, int64_t>;

		templates["llll)v"] = (void*)&jniNativeTrampolineTemplate<void, int64_t, int64_t, int64_t, int64_t>;
		templates["llll)B"] = (void*)&jniNativeTrampolineTemplate<bool, int64_t, int64_t, int64_t, int64_t>;
		templates["llll)i"] = (void*)&jniNativeTrampolineTemplate<int32_t, int64_t, int64_t, int64_t, int64_t>;
		templates["llll)l"] = (void*)&jniNativeTrampolineTemplate<int64_t, int64_t, int64_t, int64_t, int64_t>;

		templates["f)f"] = (void*)&jniNativeTrampolineTemplate<float,float>;
		templates["d)d"] = (void*)&jniNativeTrampolineTemplate<double,double>;

		templates["id)v"] = (void*)&jniNativeTrampolineTemplate<void, int32_t, double>;
		templates["ld)v"] = (void*)&jniNativeTrampolineTemplate<void, int64_t, double>;

		templates[")B"] = (void*)&jniNativeTrampolineTemplate<bool>;
		templates[")v"] = (void*)&jniNativeTrampolineTemplate<void>;
		templates[")B"] = (void*)&jniNativeTrampolineTemplate<bool>;
		templates["i)b"] = (void*)&jniNativeTrampolineTemplate<bool, int32_t>;
		templates["l)b"] = (void*)&jniNativeTrampolineTemplate<bool, int64_t>;
		templates["ll)l"] = (void*)&jniNativeTrampolineTemplate<int64_t, int64_t, int64_t>;
		//... autogenerate these!

    std::vector<size_t> fptrs;
    fptrs.reserve(templates.size());
    for (auto &p : templates) {
      fptrs.push_back((size_t)p.second);
    }
    std::sort(fptrs.begin(), fptrs.end());
    size_t maxGap = 0;
    size_t lastPtr = 0;
    for (auto i = 0; i < fptrs.size(); i++) {
      auto f = fptrs[i];
      if (i > 0) {
        auto gap = f - lastPtr;
        // printf("gap = %ld\n", gap);
        if (gap > maxGap) {
          maxGap = gap;
        }
      }
      lastPtr = f;
    }
    maxFnSize = maxGap;

    printf("maxFnSize = %ld\n", maxFnSize);
	}

  void *tmpl = templates[signature];
  if (!tmpl) {
    printf("Found no call template for sig %s\n", signature);
	  return 0L;
	}

  // auto fn = tmpl;
  // auto size = maxFnSize; //sizeOfFunction(f);
  auto size = 512;
  void *fn = copyFunction(tmpl, size);
  // if (fn) printf("Copied fn\n");
  if (!replaceSizeTValueInBuffer((char*)fn, size, SIGIL_PTR_VALUE1, (size_t)fptr)) {
    printf("Failed to find sigil value in function!\n");
    return 0L;
  }
  return fn;
}
}

/*
template <class RetType, class... ArgTypes>
RetType callbackTrampolineTemplate(ArgTypes... args) {
	typedef RetType (*FnType)(jobject, ...ArgTypes);
	return ((FnType)SIGIL_PTR_VALUE1)((jobject)SIGIL_PTR_VALUE2, &args...);
}

void *getJNICaller(JNIEnv* env, const char* signature) {
	switch (getReturnSig(signature[strlen(signature) - 1])) {
	  case DC_SIGCHAR_INT:
	  case DC_SIGCHAR_UINT:
	  	return env->CallIntMethod;
	  case DC_SIGCHAR_SHORT:
	  case DC_SIGCHAR_USHORT:
	  	return env->CallShortMethod;
	  case DC_SIGCHAR_LONG:
	  case DC_SIGCHAR_ULONG:
	  	return env->CallLongMethod;
	  default:
	  	return nullptr;
	}
}

void* getCallbackTrampoline(const char* signature, JNIEnv *env, jobject obj, void *f) {
	static std::map<std::string, void*> templates;
	if (templates.empty()) {
		templates[")b"] = &callbackTrampolineTemplate<bool>;
		templates["i)b"] = &callbackTrampolineTemplate<bool, int>;
		templates["l)b"] = &callbackTrampolineTemplate<bool, long>;
		templates["ll)l"] = &callbackTrampolineTemplate<long, long, long>;
		//... autogenerate these!
	}
	void *tmpl = templates[signature];
	if (!tmpl) {
	  return nullptr;
	}
  void *fn = copyFunction(tmpl);
  void *caller = getJNICaller(env, signature);
  if (!replaceSizeTValueInBuffer((char*)fn, SIGIL_PTR_VALUE1, (size_t)caller)
      || !replaceSizeTValueInBuffer((char*)fn, SIGIL_PTR_VALUE2, (size_t)fn)) {
    return nullptr;
  }
  return fn;
}
*/
