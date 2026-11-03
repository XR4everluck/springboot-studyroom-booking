@echo off
setlocal enabledelayedexpansion
chcp 936 >nul
cd /d "%~dp0"

rem =====================================================================
rem  上岸自习室预订系统 一键停止脚本
rem  停止范围：单端口统一应用(8080)、增强门户(8090，若在运行)、项目内置 Redis(6379)
rem  说明：MySQL 为本机系统服务，本脚本不停止；需要时请在 services.msc 手动操作
rem  用法：
rem    stop.bat              停止应用与内置 Redis（与 一键启动.bat 启动的服务对称）
rem    stop.bat app          仅停止应用(8080/8090)，保留 Redis
rem  定位方式：按端口/可执行路径精确匹配，与 一键启动.bat 的 UNI_PORT/PORTAL_PORT 对应，
rem           不会误杀其它 Java/Redis 进程
rem  注意：本文件必须以 GBK(cp936) 保存，另存为 UTF-8 会导致 cmd 解析中文错位。
rem =====================================================================

set "UNI_PORT=8080"
set "PORTAL_PORT=8090"
set "REDIS_PORT=6379"
set "STOP_REDIS=1"
if /i "%~1"=="app" set "STOP_REDIS=0"

echo [1/3] 停止单端口统一应用（端口 %UNI_PORT%）...
call :kill_port %UNI_PORT% "studyroom-uni"

echo [2/3] 停止增强门户（端口 %PORTAL_PORT%，若在运行）...
call :kill_port %PORTAL_PORT% "studyroom-portal"

if "%STOP_REDIS%"=="1" (
    echo [3/3] 停止项目内置 Redis（端口 %REDIS_PORT%，仅限 tools\redis 启动的实例）...
    call :stop_builtin_redis
) else (
    echo [3/3] app 模式：保留 Redis 运行。
)

echo.
echo   执行完毕。MySQL 为系统服务，本脚本不触碰。
if /i not "%~2"=="nopause" pause
goto :eof

rem ---------------------------------------------------------------------
rem  按端口杀进程：kill_port 端口 名称（IPv4/IPv6 双栈会报同一 PID，按 PID 去重）
rem ---------------------------------------------------------------------
:kill_port
set "port=%~1"
set "label=%~2"
set "found=0"
set "last="
for /f "tokens=5" %%p in ('netstat -ano ^| findstr LISTENING ^| findstr /C:":%port% "') do (
    if not "%%p"=="!last!" (
        set "found=1"
        echo   结束进程 PID=%%p（%label%，端口 %port%）...
        taskkill /F /PID %%p >nul 2>nul
        if errorlevel 1 echo   [警告] 结束 PID=%%p 失败，可能需要管理员权限，请手动关闭对应命令行窗口。
    )
    set "last=%%p"
)
if "%found%"=="0" echo   %label%（端口 %port%）未在运行，跳过。
goto :eof

rem ---------------------------------------------------------------------
rem  仅停止由项目内置目录启动的 redis-server.exe（按可执行路径/命令行匹配，
rem  兼容正反斜杠，避免误伤系统 Redis）
rem ---------------------------------------------------------------------
:stop_builtin_redis
powershell -NoProfile -Command "$p=Get-CimInstance Win32_Process -Filter \"Name='redis-server.exe'\" | Where-Object {($_.ExecutablePath -like '*\tools\redis\*') -or ($_.CommandLine -like '*tools\redis*') -or ($_.CommandLine -like '*tools/redis*')}; if($p){$p | ForEach-Object {Stop-Process -Id $_.ProcessId -Force; Write-Output ('  已停止内置 Redis (PID=' + $_.ProcessId + ')')}} else {Write-Output '  内置 Redis 未在运行，跳过。'}"
goto :eof
