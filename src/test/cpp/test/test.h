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
#ifndef _TEST_H
#define _TEST_H

#include "common.h"

#include <string>

typedef enum ETest {
	eFirst,
	eSecond,
	eThird
} ETest;

class TEST_API Ctest {
public:
	int firstField;
	int secondField;
	Ctest();
	Ctest(int firstField);
	//virtual 
	~Ctest();
	virtual int testVirtualAdd(int a, int b);
	int testAdd(int a, int b);
	virtual int __stdcall testVirtualAddStdCall(void* ptr, int a, int b);
	int __stdcall testAddStdCall(void* ptr, int a, int b);
	
	static void static_void();
	static Ctest* getInstance();
	static ETest* getEnum();
	static int* getInt();
};

TEST_API int testIndirectVirtualAdd(Ctest* pTest, int a, int b);

class TEST_API Ctest2 : public Ctest {
	int* fState;
	int fDestructedState;
public:
	Ctest2();
	//virtual 
	~Ctest2();
	void setState(int* pState);
	void setDestructedState(int destructedState);
	virtual int testVirtualAdd(int a, int b);
	int testAdd(int a, int b);
	const std::string& toString();
	static Ctest* getCtestInstance();
};

template <int n, typename T>
class TEST_API InvisibleSourcesTemplate {
public:
	InvisibleSourcesTemplate(int arg);
	T* createSome();
	void deleteSome(T* pValue);
};

template <typename T>
class TEST_API Temp1 {
public:
	virtual ~Temp1() {}
	void temp(T);
};

template <typename T1, typename T2>
class TEST_API Temp2 {
public:
	virtual ~Temp2() {}
	void temp(T1, T2);
};

template <typename T, int V>
class TEST_API TempV {
public:
	virtual ~TempV() {}
	void temp(T);
};

extern TEST_API int ntest;
TEST_API Ctest* createTest();

TEST_API ETest testEnum(ETest e);
TEST_API ETest testVoidEnum();
TEST_API ETest testIntEnum(int i, ETest e);

extern "C" {
	TEST_API void __cdecl voidTest();
	TEST_API double __cdecl sinInt(int);
	TEST_API double __cdecl testSum(const double *values, size_t n);
	TEST_API double __cdecl testSumi(const double *values, int n);
	TEST_API long long __cdecl testSumll(const double *values, int n);
	TEST_API int __cdecl testSumInt(const double *values, int n);
	TEST_API void __cdecl testInPlaceSquare(double *values, size_t n);

	TEST_API void __cdecl setLastWindowsError();
}

class TEST_API Module {
public: 
    virtual ~Module() {}; 
    virtual int add(int a, int b); 
};

class TEST_API IModule {
public: 
    virtual ~IModule() {}; 
    virtual int add(int a, int b) = 0; 
    virtual int subtract(int a, int b) = 0; 
};

class TEST_API AModule : public IModule {
public:
    AModule();
    virtual ~AModule();
    virtual int add(int a, int b);
    virtual int subtract(int a, int b);
};

class TEST_API IVirtual {
public:
	virtual ~IVirtual() {}
	virtual int add(int a, int b) = 0;
};

TEST_API int testIVirtualAdd(IVirtual* pVirtual, int a, int b);


typedef int (*fun_iii)(int, int);
TEST_API fun_iii getAdder();
TEST_API int forwardCall(fun_iii f, int a, int b);

class TEST_API Constructed
{
	int m_i;
	float m_b;
	char m_c;
	const char *m_x, *m_y;
public :
	Constructed(int i, float b, char c, const char** result);
	Constructed(const char *x, const char *y, const char** result);
	static size_t sizeOf();
};

struct MyUnknownStruct;
TEST_API MyUnknownStruct *newMyUnknownStruct(int a);
TEST_API int deleteMyUnknownStruct(MyUnknownStruct *s);

TEST_API char* incrPointer(char* ptr);

class TEST_API Hello
{
 public:
  Hello(const int& a, const int& b);
  ~Hello();

  int Sum();
 private:
  int a;
  int b;
};


#endif // _TEST_H
