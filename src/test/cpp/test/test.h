#ifdef __GNUC__
	#define TEST_API
	#define __cdecl
	#define __stdcall
#else
	#ifdef TEST_EXPORTS
		#define TEST_API __declspec(dllexport)
	#else
		#define TEST_API __declspec(dllimport)
	#endif
#endif

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
