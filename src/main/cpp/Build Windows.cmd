@echo off
setlocal enabledelayedexpansion

call SetEnv.cmd

for %%F in (java java\build java\build\classes) do mkdir %%F >NUL 2>NUL

"%JAVA_HOME%\bin\javac.exe" -d java\build\classes -classpath java\src java\src\bridj\*.java java\src\com\ochafik\jni\*.java java\src\com\ochafik\jni\ann\*.java

"%JAVA_HOME%\bin\javah.exe" -d jni -classpath java\build\classes com.nativelibs4java.runtime.JNI com.nativelibs4java.runtime.DynCall com.nativelibs4java.runtime.DynCall com.nativelibs4java.runtime.Platform

pushd ..\buildsys\vs2008
call BuildAll.cmd nopause
popd

pause
