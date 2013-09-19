#ifndef _BRIDJ_EXAMPLE_H
#define _BRIDJ_EXAMPLE_H

class SomeClass {
	int m_value;
public:
	SomeClass(int value);
	int someMethod(const char* message);
};

void someFunction(const char* message);

#endif // _BRIDJ_EXAMPLE_H
