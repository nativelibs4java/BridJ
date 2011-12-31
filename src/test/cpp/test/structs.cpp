
#include "stdafx.h"
#include "test.h"
#include "jni.h"
#include "math.h"

#include <iostream>
#include <string>
#include <vector>
#include <stdarg.h>

using namespace std;

struct S_int {
	int a;	
};

struct S_int2 {
	int a, b;	
};

struct S_jlong4 {
	jlong a, b, c, d;	
};

struct S_jlong10 {
	jlong a[10];	
};

TEST_API jint incr(S_int s) {
	return s.a + 1;
}
TEST_API jint sum(S_int2 s) {
	return s.a + s.b;
}
TEST_API jlong sum(S_jlong4 s) {
	return s.a + s.b + s.c + s.d;
}
TEST_API jlong sum(S_jlong10 s) {
	jlong tot = 0;
	for (int i = 0; i < 10; i++)
		tot += s.a[i];
	return tot;
}

