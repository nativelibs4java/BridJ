package org.bridj;

public interface DynamicCallback<R> {
	R apply(Object... args);
}
