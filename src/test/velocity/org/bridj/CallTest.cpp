#pragma warning(disable: 4244)

#foreach ($prim in $primitivesNoBool)

TEST_API j${prim.Name} test_incr_${prim.Name}(j${prim.Name} value) {
	return (j${prim.Name})(value + 1);
}

TEST_API void test_incr_${prim.Name}_out(j${prim.Name} value, j${prim.Name}* pOut) {
	*pOut = test_incr_${prim.Name}(value);
}
TEST_API j${prim.Name} test_callback_${prim.Name}_${prim.Name}(j${prim.Name} (*cb)(j${prim.Name}), j${prim.Name} value) {
	j${prim.Name} ret = cb(value);
	return ret;
}

TEST_API jdouble test_callback_${prim.Name}_${prim.Name}_asDouble(j${prim.Name} (*cb)(j${prim.Name}), j${prim.Name} value) {
	return (jdouble)test_callback_${prim.Name}_${prim.Name}(cb, value);
}

#foreach ($n in [2, 9, 20])

TEST_API j${prim.Name} test_add${n}_${prim.Name}(#foreach ($i in [1..$n])#if($i > 1), #end j${prim.Name} arg$i#end) {
	j${prim.Name} tot = (j${prim.Name})0;
	j${prim.Name} fact = (j${prim.Name})2;
#foreach ($i in [1..$n])
	j${prim.Name} v$i = (j${prim.Name})(fact * ($i + 1));
	fact *= (j${prim.Name})2;
	tot += v$i;
#end
	return tot;
}
TEST_API void test_add${n}_${prim.Name}_out(#foreach ($i in [1..$n])#if($i > 1), #end j${prim.Name} arg$i#end, j${prim.Name}* pOut) {
	*pOut = test_add${n}_${prim.Name}(#foreach ($i in [1..$n])#if($i > 1), #end arg$i#end);
}

#end

#end


