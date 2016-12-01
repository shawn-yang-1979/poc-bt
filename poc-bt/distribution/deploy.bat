REM Copy all docker scripts to a remote machine.
set scp=D:\Program\pscp
set username=pi
set password=raspberry
set ip=192.168.10.5
set folder=/home/pi/program/

%scp% -pw %password% -v -p %~dp0..\target\*.jar %username%@%ip%:%folder%