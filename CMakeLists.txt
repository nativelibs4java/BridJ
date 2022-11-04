cmake_minimum_required(VERSION 3.18.0)
project(BridJ C CXX)

set(CMAKE_CXX_STANDARD 11)

if (${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
  enable_language(OBJC)
endif()

# INTERPROCEDURAL_OPTIMIZATION
cmake_policy(SET CMP0069 NEW) 
set(CMAKE_POLICY_DEFAULT_CMP0069 NEW)
set(CMAKE_INTERPROCEDURAL_OPTIMIZATION TRUE)

set (CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR})
set (CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR})

option(BUILD_TESTS "Whether to build tests" ON)

add_subdirectory(dyncall)
add_subdirectory(src/main/cpp/bridj)
add_subdirectory(src/test/cpp/test) #  EXCLUDE_FROM_ALL
add_subdirectory(src/test/cpp/dependsOnTest)
add_subdirectory(Examples/BasicExample/src/main/native/example)