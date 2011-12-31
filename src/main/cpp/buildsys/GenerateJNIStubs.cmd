@echo off
setlocal enabledelayedexpansion

"%JAVA_HOME%\bin\javah.exe" -d ..\bridj -classpath ..\..\..\..\target\classes org.bridj.JNI org.bridj.BridJ org.bridj.Pointer org.bridj.Platform

