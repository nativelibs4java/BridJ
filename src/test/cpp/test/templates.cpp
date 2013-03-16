/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2013, Olivier Chafik (http://ochafik.com/)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Olivier Chafik nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY OLIVIER CHAFIK AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
