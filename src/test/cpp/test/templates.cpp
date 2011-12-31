
#include "stdafx.h"
#include "test.h"

#include <iostream>
#include <vector>
#include <string>

using namespace std;


template <typename T>
vector<T> newVector(int n) {
	vector<T> v;
	for (int i = 0; i < n; i++)
		v.push_back((T)i);
	return v;
}
template <typename T>
void push_back(vector<T>& v, const T* value) {
	v.push_back(*value);
}
template <typename T>
void clear(vector<T>& v) {
	v.clear();
}
template <typename T>
void resize(vector<T>& v, int n) {
	v.resize(n);
}


template <typename T>
int sizeofVector() {
	return sizeof(vector<T>);
}

#define VECTOR_OPS(t) \
template vector<t> TEST_API newVector<t>(int);

VECTOR_OPS(int);
VECTOR_OPS(long long);
VECTOR_OPS(double);
VECTOR_OPS(float);

template int TEST_API sizeofVector<int>();
template int TEST_API sizeofVector<long long>();
template int TEST_API sizeofVector<double>();
template int TEST_API sizeofVector<float>();

typedef vector<int> (*IntVecFun)(int);
typedef int (*SizeTFun)();

extern "C" {
TEST_API IntVecFun newIntVector = newVector<int>;
TEST_API SizeTFun sizeofIntVector = sizeofVector<int>;
}

TEST_API void toto() {}
