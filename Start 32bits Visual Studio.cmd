@echo off
setlocal

cd src\main\cpp\buildsys\vs2008
call SetEnv.cmd
setlocal enabledelayedexpansion
call CopyRulesIfNeeded.cmd
call "c:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\vcvarsall.bat" x86

start bridj.sln