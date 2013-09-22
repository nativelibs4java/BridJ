#include "testNamespaces.h"

#include <iostream>

using namespace std;

com::nativelibs4java::bridj::FullyNamespacedClass::FullyNamespacedClass(int value): m_value(value) {
	cout << "Built a com::nativelibs4java::bridj::FullyNamespacedClass" << endl;
};
int com::nativelibs4java::bridj::FullyNamespacedClass::getValue() { return m_value; }
int com::nativelibs4java::bridj::FullyNamespacedClass::getEnumValuePlus1(com::nativelibs4java::bridj::FullyNamespacedEnum e) {
	return 1 + (int)e;
}

SimplyNamespacedClass::SimplyNamespacedClass(int value): m_value(value + 100) {
	cout << "Built a SimplyNamespacedClass" << endl;
};
int SimplyNamespacedClass::getValue() { return m_value; }

bridj::SimplyNamespacedClass::SimplyNamespacedClass(int value): m_value(value) {
	cout << "Built a bridj::SimplyNamespacedClass" << endl;
};
int bridj::SimplyNamespacedClass::getValue() { return m_value; }
