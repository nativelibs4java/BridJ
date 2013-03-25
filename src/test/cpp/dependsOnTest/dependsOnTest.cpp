#include "stdafx.h"
#include "test.h"

DEPENDSONTEST_API int addThatDependsOnTest(int a, int b) {
	return forwardCall(getAdder(), a, b);
}

