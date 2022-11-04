cmake_minimum_required(VERSION 3.18.0)
project(BridJ-example)

# if ("${FORCE_JAVA_HOME}" STREQUAL "")
#   find_package(JNI REQUIRED)
# else()
#   SET(JNI_INCLUDE_DIRS
#       ${FORCE_JAVA_HOME}/include
#       ${FORCE_JAVA_HOME}/include/linux
#       ${FORCE_JAVA_HOME}/include/solaris
#       ${FORCE_JAVA_HOME}/include/windows
#       ${FORCE_JAVA_HOME}/include/win32
#       ${FORCE_JAVA_HOME}/include/darwin
#   )
#   set(JNI_LIBRARIES ${FORCE_JAVA_HOME}/lib/server/libjvm.dylib)
# endif()
# include_directories(${JNI_INCLUDE_DIRS})

include_directories(include)

add_compile_options( -fPIC )
if (${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
  add_compile_options( -stdlib=libc++ )
endif()

add_link_options(-l stdc++)
if (${CMAKE_SYSTEM_NAME} MATCHES "SunOS")
  # http://gcc.gnu.org/onlinedocs/gcc/Solaris-2-Options.html
  add_link_options(-mimpure-text)
endif()

add_library(example SHARED example.cpp)
target_link_libraries(example PRIVATE ${JNI_LIBRARIES})
