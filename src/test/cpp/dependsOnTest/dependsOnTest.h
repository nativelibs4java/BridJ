#ifdef __GNUC__
	#define DEPENDSONTEST_API
	#define __cdecl
	#define __stdcall
#else
	#ifdef DEPENDSONTEST_EXPORTS
	#define DEPENDSONTEST_API __declspec(dllexport)
	#else
	#define DEPENDSONTEST_API __declspec(dllimport)
	#endif
#endif

class DEPENDSONTEST_API CdependsOnTest {
public:
	CdependsOnTest(void);
};

extern DEPENDSONTEST_API int ndependsOnTest;

DEPENDSONTEST_API int fndependsOnTest(void);
