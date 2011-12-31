@echo off
setlocal

cd src\main\cpp\buildsys\vs2008
call SetEnv.cmd
setlocal enabledelayedexpansion
call CopyRulesIfNeeded.cmd

start bridj.sln