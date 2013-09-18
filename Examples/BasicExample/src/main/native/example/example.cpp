#include <iostream>

#include "example.h"

using namespace std;

SomeClass::SomeClass(int value): m_value(value) {
	cout << "Building SomeClass with " << value << endl;
}

void SomeClass::someMethod(const char* message) {
	cout << "SomeClass(" << m_value << ").someMethod(" << message << ")" << endl;
}

void someFunction(const char* message) {
	cout << "someFunction(" << message << ")" << endl;
}
