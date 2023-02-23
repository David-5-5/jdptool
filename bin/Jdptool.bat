echo off

set PROFILER_HOME=%cd%\..

set JPDA_TRANSPORT=dt_socket
set JPDA_ADDRESS=55000
set JPDA_NAME=profiler
set TRACE_CLASS=net.sf.jdptool.JdpEntry
set MAIN_CLASS=net.sf.jdptool.Target
set CONFIG_DIR=%PROFILER_HOME%\runtime\conf
set TRACE_PARAM=-config %CONFIG_DIR%\test-jdp-config.xml
set JAVA_OPTS=-Djdptool.home=%PROFILER_HOME%

if not "%JAVA_HOME%"=="" goto jdkset
set JAVA_HOME="C:\Program Files\Java\jdk1.8.0_301"

:jdkset
rem set classpath
set ORG_CLASSPATH=%CLASSPATH%
set CLASSPATH=%PROFILER_HOME%\dist\classes;%JAVA_HOME%\lib\tools.jar
for /R "%PROFILER_HOME%\lib" %%i in (*.jar) do call lcp.bat %%i


echo Using PROFILER_HOME:   %PROFILER_HOME%
echo Using JAVA_HOME:       %JAVA_HOME%
rem echo USING CLASSPATH:		%CLASSPATH%

if exist "%PROFILER_HOME%\bin\setevn.bat" call "%PROFILER_HOME%\bin\setevn.bat"

if not ""%1""==""launch"" goto nolaunch
echo %JAVA_HOME%\bin\java %JAVA_OPTS% -cp %CLASSPATH% %TRACE_CLASS% %TRACE_PARAM% %MAIN_CLASS%
%JAVA_HOME%\bin\java %JAVA_OPTS% -cp %CLASSPATH% %TRACE_CLASS% %TRACE_PARAM% %MAIN_CLASS%
goto end

:nolaunch
if not ""%1""==""attach"" goto noattach
if not ""%2""==""target"" goto trace
echo %JAVA_HOME%\bin\java -Xdebug -Xnoagent -Xrunjdwp:transport=%JPDA_TRANSPORT%,server=y,suspend=n,address=%JPDA_ADDRESS% -cp %CLASSPATH% %MAIN_CLASS%
%JAVA_HOME%\bin\java -Xdebug -Xnoagent -Xrunjdwp:transport=%JPDA_TRANSPORT%,server=y,suspend=n,address=%JPDA_ADDRESS% -cp %CLASSPATH% %MAIN_CLASS%
goto end
:trace
echo %JAVA_HOME%\bin\java %JAVA_OPTS% -cp %CLASSPATH% %TRACE_CLASS% -attach transport=%JPDA_TRANSPORT%,port=%JPDA_ADDRESS%,name=%JPDA_NAME% %TRACE_PARAM%
%JAVA_HOME%\bin\java %JAVA_OPTS% -cp %CLASSPATH% %TRACE_CLASS% -attach transport=%JPDA_TRANSPORT%,port=%JPDA_ADDRESS%,name=%JPDA_NAME% %TRACE_PARAM%
goto end

:noattach
if not ""%1""==""listen"" goto helpcmd
if not ""%2""==""trace"" goto target
echo %JAVA_HOME%\bin\java %JAVA_OPTS% -cp %CLASSPATH% %TRACE_CLASS% -listen transport=%JPDA_TRANSPORT%,port=%JPDA_ADDRESS%,name=%JPDA_NAME% %TRACE_PARAM%
%JAVA_HOME%\bin\java %JAVA_OPTS% -cp %CLASSPATH% %TRACE_CLASS% -listen transport=%JPDA_TRANSPORT%,port=%JPDA_ADDRESS%,name=%JPDA_NAME% %TRACE_PARAM%
goto end
:target
echo %JAVA_HOME%\bin\java -Xdebug -Xnoagent -Xrunjdwp:transport=%JPDA_TRANSPORT%,server=n,suspend=y,address=%JPDA_ADDRESS% -cp %CLASSPATH% %MAIN_CLASS%
%JAVA_HOME%\bin\java -Xdebug -Xnoagent -Xrunjdwp:transport=%JPDA_TRANSPORT%,server=n,suspend=y,address=%JPDA_ADDRESS% -cp %CLASSPATH% %MAIN_CLASS%
goto end

:helpcmd
echo "Usage: jdptool <launch|attach|listen>  <target|trace>"

:end
set CLASSPATH=%ORG_CLASSPATH%
