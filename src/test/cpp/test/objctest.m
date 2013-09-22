#if defined(__APPLE__) || defined(__Darwin__)

#include "objctest.h"

@implementation DelgHolder

	@synthesize delegate;
	
	- (int)outerAdd:(int)a to:(int)b {
		return [[self delegate] add: a to: b];	
	}
@end

@implementation DelgImpl
	- (int)add:(int)a to:(int)b {
		return a + b;
	}
@end

//Foo *obj = [[Foo alloc] init];
//[obj setDelegate:self];

int forwardBlockCallIntIntInt(int (^block)(int, int), int a, int b) 
{
	return block(a, b);	
}

#endif // defined(__APPLE__) || defined(__Darwin__)
