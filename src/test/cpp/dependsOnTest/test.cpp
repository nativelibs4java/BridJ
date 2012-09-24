#include "test.h"

int addThatDependsOnTest(int a, int b) {
	return forwardCall(getAdder(), a, b);
}

