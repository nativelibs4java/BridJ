@echo off

if "%DYNCALL_HOME%" == "" set DYNCALL_HOME=..\..\..\..\..\..\..\..\..\..\dyncall
if not exist "%DYNCALL_HOME%" set DYNCALL_HOME=c:\src\dyncall
if not exist "%DYNCALL_HOME%" set DYNCALL_HOME=f:\Experiments\tmp\dyncall
if not exist "%DYNCALL_HOME%" set DYNCALL_HOME=f:\Experiments\tmp\key\svn\dyncall
if not exist "%DYNCALL_HOME%" set DYNCALL_HOME=c:\Prog\dyncall


