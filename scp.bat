@echo on
setlocal

rem 设置变量
set LOCAL_FILE=C:\Users\76769\Desktop\demo\demo\pom.xml
set REMOTE_USER=aidlux
set REMOTE_HOST=192.168.2.124
set REMOTE_PATH=/home/aidlux/
set REMOTE_FILE=%REMOTE_PATH%pom.xml

rem 使用SCP命令拷贝文件
scp %LOCAL_FILE% %REMOTE_USER%@%REMOTE_HOST%:%REMOTE_FILE%

rem 检查命令是否成功执行
if %errorlevel% neq 0 (
    echo 文件拷贝失败，请检查连接和路径设置。
    exit /b 1
) else (
    echo 文件已成功拷贝到远程主机。
)

endlocal
