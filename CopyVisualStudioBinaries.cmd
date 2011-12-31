@echo off
setlocal

set VS_DIR=src\main\cpp\buildsys\vs2008\Debug

copy %VS_DIR%\bridj.dll src\main\resources\win32
copy %VS_DIR%\bridj.pdb src\main\resources\win32
copy %VS_DIR%\test.dll src\test\resources\win32 
copy %VS_DIR%\test.pdb src\test\resources\win32

rem pause
