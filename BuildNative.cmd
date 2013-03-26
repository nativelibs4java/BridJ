@echo off
setlocal enabledelayedexpansion

pushd .
cd src\main\cpp\buildsys\vs2008

call SetEnv.cmd
call CopyRulesIfNeeded.cmd

pushd ..
call GenerateJNIStubs.cmd
popd

if "%CONFIG%" == "" set CONFIGS=Release Debug
if not "%CONFIG%" == "" set CONFIGS=%CONFIG%
if "%CONFIG%" == "" set CONFIG=Release

for %%C in (%CONFIGS%) do (
	REM if exists "c:\Program Files (x86)" 
	call "c:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\vcvarsall.bat" x86
	REM if not exists "c:\Program Files (x86)" call "c:\Program Files\Microsoft Visual Studio 9.0\VC\vcvarsall.bat" x86
	devenv /nologo /useenv /Build "%%C|Win32" bridj.sln
	echo.
	
	REM if exists "c:\Program Files (x86)" 
	call "c:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\vcvarsall.bat" x86_amd64
	REM if not exists "c:\Program Files (x86)" call "c:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\vcvarsall.bat" x86_amd64
	devenv /nologo /useenv /Build "%%C|x64" bridj.sln
	echo.
)

for %%T in (win32 win64) do (
	del ..\..\..\resources\org\bridj\lib\%%T\*.dll
	del ..\..\..\..\test\resources\org\bridj\lib\%%T\*.dll
)

rem set CONFIG=Debug
rem set CONFIG=Release

copy %CONFIG%\bridj.dll ..\..\..\resources\org\bridj\lib\win32
copy %CONFIG%\test.dll ..\..\..\..\test\resources\org\bridj\lib\win32
copy %CONFIG%\dependsOnTest.dll ..\..\..\..\test\resources\org\bridj\lib\win32

copy x64\%CONFIG%\bridj.dll ..\..\..\resources\org\bridj\lib\win64
copy x64\%CONFIG%\test.dll ..\..\..\..\test\resources\org\bridj\lib\win64
copy x64\%CONFIG%\dependsOnTest.dll ..\..\..\..\test\resources\org\bridj\lib\win64

rem if "%CONFIG%" == "Debug" (

	copy %CONFIG%\bridj.pdb ..\..\..\resources\org\bridj\lib\win32
	copy %CONFIG%\test.pdb ..\..\..\..\test\resources\org\bridj\lib\win32
	
	copy x64\%CONFIG%\bridj.pdb ..\..\..\resources\org\bridj\lib\win64
	copy x64\%CONFIG%\test.pdb ..\..\..\..\test\resources\org\bridj\lib\win64
rem )
rem echo Copied files from %CONFIG% :
rem dir %CONFIG%\bridj.dll x64\%CONFIG%\bridj.dll


popd

if not "%1" == "nopause" pause
