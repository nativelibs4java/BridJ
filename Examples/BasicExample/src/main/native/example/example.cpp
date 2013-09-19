#include <iostream>

#include "example.h"

using namespace std;

SomeClass::SomeClass(int value): m_value(value) {
	cout << "Building SomeClass with " << value << endl;
}

int SomeClass::someMethod(const char* message) {
	cout << "SomeClass(" << m_value << ").someMethod(" << message << ")" << endl;
	return m_value;
}

void someFunction(const char* message) {
	cout << "someFunction(" << message << ")" << endl;
}
