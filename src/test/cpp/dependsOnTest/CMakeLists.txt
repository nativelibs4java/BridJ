cmake_minimum_required(VERSION 3.18.0)
project(BridJ-dependsOnTest)

include_directories(${PROJECT_SOURCE_DIR}/../test)
if (${CMAKE_SYSTEM_NAME} MATCHES "Windows")
  include_directories("C:\\Program Files\\Microsoft SDKs\\Windows\\v6.0A\\Include")
endif()

add_library(dependsOnTest SHARED dependsOnTest.cpp)
add_compile_definitions(DEPENDSONTEST_EXPORTS=1)
add_compile_options(dependsOnTest PRIVATE -fPIC)

if (${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
  add_compile_options(dependsOnTest PRIVATE -stdlib=libc++)
endif()

if (${CMAKE_SYSTEM_NAME} MATCHES "SunOS")
  # http://gcc.gnu.org/onlinedocs/gcc/Solaris-2-Options.html
  add_link_options(dependsOnTest PRIVATE -mimpure-text)
else()
  add_link_options(dependsOnTest PRIVATE
    -Wl,-rpath
    '-Wl,$$ORIGIN'
    -Wl,--enable-new-dtags
  )
endif()

target_link_libraries(dependsOnTest PRIVATE test)
