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
#include <list>
#include <string>

using namespace std;


template <typename T>
vector<T>* new_vector(int n) {
	vector<T> *v = new vector<T>();
	for (int i = 0; i < n; i++)
		v->push_back((T)i);
	return v;
}
template <typename T>
void delete_vector(vector<T>* v) {
    delete v;
}

template <typename T>
list<T>* new_list() {
	return new list<T>();
}
template <typename T>
void delete_list(list<T>* v) {
    delete v;
}

template <typename T>
T vector_get(vector<T>* v, int i) {
	return (*v)[i];
}

template <typename T>
void vector_set(vector<T>* v, int i, T value) {
	(*v)[i] = value;
}

template <typename T>
void vector_push_back(vector<T>* v, T value) {
	v->push_back(value);
}

template <typename T>
void list_push_back(list<T>* v, T value) {
	v->push_back(value);
}
template <typename T>
void vector_clear(vector<T>* v) {
	v->clear();
}
template <typename T>
void vector_resize(vector<T>* v, int n) {
	v->resize(n);
}


template <typename T>
size_t sizeofVector() {
	return sizeof(vector<T>);
}

#define VECTOR_OPS(t) \
template vector<t>* TEST_API new_vector<t>(int); \
template int TEST_API vector_get<t>(vector<t>*, int); \
template void TEST_API vector_set<t>(vector<t>*, int, t); \
template void TEST_API vector_push_back<t>(vector<t>*, t); \
template void TEST_API delete_vector<t>(vector<t>*);

#define LIST_OPS(t) \
template list<t>* TEST_API new_list<t>(); \
template void TEST_API list_push_back<t>(list<t>*, t); \
template void TEST_API delete_list<t>(list<t>*);

VECTOR_OPS(int);
//VECTOR_OPS(long long);
//VECTOR_OPS(double);
//VECTOR_OPS(float);

LIST_OPS(int);
//LIST_OPS(long long);
//LIST_OPS(double);
//LIST_OPS(float);

template size_t TEST_API sizeofVector<int>();
//template size_t TEST_API sizeofVector<long long>();
//template size_t TEST_API sizeofVector<double>();
//template size_t TEST_API sizeofVector<float>();

typedef vector<int>* (*Fun_IntVector_Int)(int);
typedef void (*Fun_Void_IntVector_PInt)(vector<int>*, int);
typedef void (*Fun_Void_IntVector_Int)(vector<int>*, int);
typedef void (*Fun_Void_IntVector_Int_Int)(vector<int>*, int, int);

typedef int (*Fun_Int_IntVector_Int)(vector<int>*, int);
typedef void (*Fun_Void_IntVector)(vector<int>*);
typedef size_t (*Fun_SizeT)();

typedef list<int>* (*Fun_IntList)();
typedef void (*Fun_Void_IntList_PInt)(list<int>*, int);
typedef void (*Fun_Void_IntList)(list<int>*);

extern "C" {
TEST_API Fun_IntVector_Int new_int_vector = new_vector<int>;
TEST_API Fun_Void_IntVector_PInt int_vector_push_back = vector_push_back<int>;
TEST_API Fun_Int_IntVector_Int int_vector_get = vector_get<int>;
TEST_API Fun_Void_IntVector_Int_Int int_vector_set = vector_set<int>;
TEST_API Fun_Void_IntVector_Int int_vector_resize = vector_resize<int>;
TEST_API Fun_Void_IntVector delete_int_vector = delete_vector<int>;
TEST_API Fun_SizeT sizeof_int_vector = sizeofVector<int>;

TEST_API Fun_IntList new_int_list = new_list<int>;
TEST_API Fun_Void_IntList_PInt int_list_push_back = list_push_back<int>;
TEST_API Fun_Void_IntList delete_int_list = delete_list<int>;
}

TEST_API void toto() {}
