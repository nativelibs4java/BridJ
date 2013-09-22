#include <iostream>

#include "example.h"

using namespace std;

nl4j::SomeClass::SomeClass(int value): m_value(value) {
	cout << "Building SomeClass with " << value << endl;
}

int nl4j::SomeClass::someMethod(const char* message) {
	cout << "SomeClass(" << m_value << ").someMethod(" << message << ")" << endl;
	return m_value;
}

void nl4j::someCppFunction(const char* message) {
	cout << "someCppFunction(" << message << ")" << endl;
}

void someCFunction(const char* message) {
	cout << "someCFunction(" << message << ")" << endl;
}
