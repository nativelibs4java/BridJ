#ifndef _BRIDJ_EXAMPLE_H
#define _BRIDJ_EXAMPLE_H

namespace nl4j {
	class SomeClass {
		int m_value;
	public:
		SomeClass(int value);
		int someMethod(const char* message);
	};

	void someCppFunction(const char* message);
}

extern "C" {
	void someCFunction(const char* message);
}

#endif // _BRIDJ_EXAMPLE_H
