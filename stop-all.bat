@echo off
setlocal enabledelayedexpansion
chcp 936 >nul
cd /d "%~dp0"

rem =====================================================================
rem  上岸自习室预订系统 一键停止脚本
rem  按端口关闭后台管理端(8080)、用户移动端(8081)与内置 Redis(6379)
rem  不会停止 MySQL(3306)
rem  注意两点：
rem    1. 本文件必须以 GBK(cp936) 保存，另存为 UTF-8 会让 cmd 解析中文错位；
rem    2. echo 文案里不要出现裸的 ">"，cmd 会把它当重定向并生成乱码文件。
rem =====================================================================

echo 正在停止自习室预订系统服务...
call :kill_by_port 8080
call :kill_by_port 8081
call :kill_by_port 6379
echo 完成。若 studyroom-admin / studyroom-app / studyroom-redis 窗口仍开着，可直接关闭。
goto :eof

rem ---------------------------------------------------------------------
rem  子过程：结束监听指定端口的进程。netstat 会因 IPv4/IPv6 两行列出同一个
rem  PID，用 KILLED_<pid> 标记变量去重，同一进程只结束一次。
rem ---------------------------------------------------------------------
:kill_by_port
set "FOUND=0"
for /f "tokens=5" %%p in ('netstat -ano ^| findstr /R /C:"LISTENING" ^| findstr /C:":%~1 "') do (
    if not defined KILLED_%%p (
        set "KILLED_%%p=1"
        set "FOUND=1"
        echo   端口 %~1 已监听，结束进程 PID %%p
        taskkill /f /pid %%p >nul 2>nul
    )
)
if "!FOUND!"=="0" echo   端口 %~1 无监听进程，跳过。
goto :eof
