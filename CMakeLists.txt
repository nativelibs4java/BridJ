cmake_minimum_required (VERSION 3.24.0)
project(BridJ C CXX OBJC)

# INTERPROCEDURAL_OPTIMIZATION
cmake_policy(SET CMP0069 NEW) 
set(CMAKE_POLICY_DEFAULT_CMP0069 NEW)
set(CMAKE_INTERPROCEDURAL_OPTIMIZATION TRUE)

set (CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR})
set (CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR})

add_subdirectory(dyncall)

add_subdirectory(src/main/cpp/bridj)

add_subdirectory(src/test/cpp/test) #  EXCLUDE_FROM_ALL
add_subdirectory(src/test/cpp/dependsOnTest)
