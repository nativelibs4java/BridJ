
@echo off
setlocal enabledelayedexpansion

pushd ..\buildsys\vs2008\Debug

"%JAVA_HOME%\bin\java.exe" -classpath ..\..\..\bridj\classes bridj.Main

popd

pause
