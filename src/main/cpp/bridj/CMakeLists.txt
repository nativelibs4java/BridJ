cmake_minimum_required(VERSION 3.18.0)
project(BridJ)

option(DIRECT_CALLS "Enable raw native forward assembler on supported platform" OFF)
option(ENABLE_TRAMPOLINES "Enable experimental trampolines WIP" OFF)

if ("${FORCE_JAVA_HOME}" STREQUAL "")
  find_package(JNI REQUIRED)
else()
  set(JNI_INCLUDE_DIRS
      ${FORCE_JAVA_HOME}/include
      ${FORCE_JAVA_HOME}/include/linux
      ${FORCE_JAVA_HOME}/include/solaris
      ${FORCE_JAVA_HOME}/include/windows
      ${FORCE_JAVA_HOME}/include/win32
      ${FORCE_JAVA_HOME}/include/darwin
  )
  if (${CMAKE_SYSTEM_NAME} MATCHES "Windows")
    set(JNI_LIBRARIES ${FORCE_JAVA_HOME}/lib/jvm.lib)
  elseif (${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
    set(JNI_LIBRARIES ${FORCE_JAVA_HOME}/lib/server/libjvm.dylib)
  else()
    set(JNI_LIBRARIES ${FORCE_JAVA_HOME}/lib/server/libjvm.so)
  endif()
endif()

include_directories(${JNI_INCLUDE_DIRS})
include_directories(
  ${PROJECT_SOURCE_DIR}/../../../../dyncall/dyncall
  ${PROJECT_SOURCE_DIR}/../../../../dyncall/dyncallback
  ${PROJECT_SOURCE_DIR}/../../../../dyncall/dynload
)
# find_package(DynCall REQUIRED)
# find_package(DynCallback REQUIRED)
# find_package(DynLoad REQUIRED)
# include_directories(${DYNCALL_INCLUDE_DIRS} ${DYNCALLBACK_INCLUDE_DIRS} ${DYNLOAD_INCLUDE_DIRS})

add_compile_definitions(ENABLE_TRAMPOLINES=${ENABLE_TRAMPOLINES})
add_compile_definitions(DISABLE_DCSTRUCT=1)
if (NOT ${DIRECT_CALLS})
  add_compile_definitions(NO_DIRECT_CALLS=1)
endif()

# No funky business of overoptimizing for the build machine.

if (NOT ${CMAKE_SYSTEM_PROCESSOR} MATCHES "arm")
  add_compile_options(-mtune=generic)
endif()

list(APPEND BRIDJ_SOURCES
  CallbackHandler.c
  CppHandler.c
  Errors.c
  Exceptions.c
  FunctionHandler.c
  GenericJavaCallbackHandlers.c
  HandlersCommon.c
  JNI.c
  Platform.c
  ThreadLocal.c
  Utils.c
)
if (${ENABLE_TRAMPOLINES})
  list(APPEND BRIDJ_SOURCES Trampolines.cc)
endif()

if (${DIRECT_CALLS})
  list(APPEND BRIDJ_SOURCES
    RawNativeForwardCallback.c
    RawNativeForwardCallback_arch.s
  )
endif()

if (${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
  list(APPEND BRIDJ_SOURCES
    ObjCBlocks.m
    ObjCHandler.c
    ObjCProxy.m
  )
  add_compile_definitions(_DARWIN_C_SOURCE=)
  add_link_options(-ldl -framework Foundation -all_load)
endif()

if (NOT ${CMAKE_SYSTEM_NAME} MATCHES "Darwin|SunOS|Windows")
  add_link_options(-Wl,--hash-style=both)
endif()

add_library(bridj SHARED ${BRIDJ_SOURCES})
target_link_libraries(bridj PRIVATE ${JNI_LIBRARIES} dyncall_s dynload_s dyncallback_s)
# target_link_libraries(bridj PRIVATE ${JNI_LIBRARIES} ${DYNCALL_LIBRARIES} ${DYNCALLBACK_LIBRARIES} ${DYNLOAD_LIBRARIES})
