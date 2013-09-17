#include "bridj.hpp"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void vectorAppend(PointerVector* vector, void* value) {
	size_t bufferLength = vector->bufferLength;
	if (vector->length >= bufferLength) {
		size_t newBufferLength =
			vector->length == 0 ? 4 : (size_t)(vector->length * 1.6);
		void* oldBuffer = vector->buffer;
		vector->buffer = malloc(newBufferLength * sizeof(void*));
		vector->bufferLength = newBufferLength;
		if (oldBuffer) {
			memcpy(vector->buffer, oldBuffer, vector->length * sizeof(void*));
			free(oldBuffer);
		}
	}
	vector->buffer[vector->length++] = value;
}
