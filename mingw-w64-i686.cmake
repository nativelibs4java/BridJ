# Toolchain file for building for Windows from an Mac or Ubuntu/Debian system.
# Shamelessly copied from https://gist.github.com/peterspackman/8cf73f7f12ba270aa8192d6911972fe8
#                     and https://gist.github.com/ebraminio/2e32c8f6d032a8e01606f7f564d2b1ee 🙏
#
# Typical usage:
#    *) install cross compiler: `sudo apt-get install mingw-w64` or `brew install mingw-w64`
#    *) cmake -DCMAKE_TOOLCHAIN_FILE=$PWD/mingw-w64-x86_64.cmake -B build -S .

set(CMAKE_SYSTEM_NAME Windows)
set(TOOLCHAIN_PREFIX i686-w64-mingw32)

set(CMAKE_C_COMPILER ${TOOLCHAIN_PREFIX}-gcc)
set(CMAKE_CXX_COMPILER ${TOOLCHAIN_PREFIX}-g++)
set(CMAKE_Fortran_COMPILER ${TOOLCHAIN_PREFIX}-gfortran)
set(CMAKE_RC_COMPILER ${TOOLCHAIN_PREFIX}-windres)

set(CMAKE_FIND_ROOT_PATH /usr/${TOOLCHAIN_PREFIX})

# modify default behavior of FIND_XXX() commands
set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM NEVER)
set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY ONLY)
set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE ONLY)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -static -Os")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -static -Os")