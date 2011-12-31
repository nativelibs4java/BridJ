@echo off
setlocal

set FILE=masm64.rules
rem set TARGET_FILE=C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\VCProjectDefaults\%FILE%
rem if not exist "%TARGET_FILE%" copy "%FILE%" "%TARGET_FILE%"
if exist "C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\VCProjectDefaults" (
	if not exist "C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\VCProjectDefaults\%FILE%" copy "%FILE%" "C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\VCProjectDefaults"
)
if exist "C:\Program Files\Microsoft Visual Studio 9.0\VC\VCProjectDefaults" (
	if not exist "C:\Program Files\Microsoft Visual Studio 9.0\VC\VCProjectDefaults\%FILE%" copy "%FILE%" "C:\Program Files\Microsoft Visual Studio 9.0\VC\VCProjectDefaults"
)

