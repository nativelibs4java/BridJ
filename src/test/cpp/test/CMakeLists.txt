cmake_minimum_required(VERSION 3.18.0)
project(BridJ-Test)

if ("${FORCE_JAVA_HOME}" STREQUAL "")
  find_package(JNI REQUIRED)
else()
  SET(JNI_INCLUDE_DIRS
      ${FORCE_JAVA_HOME}/include
      ${FORCE_JAVA_HOME}/include/linux
      ${FORCE_JAVA_HOME}/include/solaris
      ${FORCE_JAVA_HOME}/include/windows
      ${FORCE_JAVA_HOME}/include/darwin
  )
  set(JNI_LIBRARIES ${FORCE_JAVA_HOME}/lib/server/libjvm.dylib)
endif()

include_directories(${JNI_INCLUDE_DIRS})
if (${CMAKE_SYSTEM_NAME} MATCHES "Windows")
  include_directories("C:\Program Files\Microsoft SDKs\Windows\v6.0A\Include")
endif()

list(APPEND TEST_SOURCES
  test.cpp
  testNamespaces.cpp
  structs.cpp
  templates.cpp
)
if (${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
  list(APPEND TEST_SOURCES objctest.m)
endif()

add_compile_options( -fPIC )
if (${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
  add_compile_options( -stdlib=libc++ )
endif()
add_link_options(-l stdc++)

if (${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
  add_link_options(
    -framework Foundation -all_load
    -install_name @rpath/libtest.dylib
  )
endif()

if (${CMAKE_SYSTEM_NAME} MATCHES "SunOS")
  # http://gcc.gnu.org/onlinedocs/gcc/Solaris-2-Options.html
  add_compile_options(
    -fno-stack-protector -funsafe-loop-optimizations
    -D__EXTENSIONS__
  )
  add_link_options(
    -mimpure-text
    -L/opt/csw/gcc4/lib
  )
endif()

add_library(test SHARED ${TEST_SOURCES})
target_link_libraries(test PRIVATE ${JNI_LIBRARIES})
