@echo off
setlocal enabledelayedexpansion
chcp 936 >nul
cd /d "%~dp0"

rem =====================================================================
rem  上岸自习室预订系统 一键启动脚本（统一门户 · 纯 Java 服务端渲染）
rem  用法：
rem    一键启动.bat            使用已有 jar 直接启动（缺失时自动构建）
rem    一键启动.bat build      先执行 mvn clean package -DskipTests 再启动
rem  依赖：JDK 17+、Maven、MySQL 8（本机服务名 MySQL）、项目内置 Redis（tools\redis）
rem  说明：本文件必须以 GBK(cp936) 保存，另存为 UTF-8 会导致 cmd 解析中文错位。
rem        echo 与 rem 注释的文案里不要写重定向符号，cmd 仍会解析并可能生成垃圾文件。
rem =====================================================================

set "PORTAL_PORT=8080"
set "REDIS_PORT=6379"
set "MYSQL_PORT=3306"
set "MYSQL_USER=root"
set "MYSQL_PWD=1234"
set "DB_NAME=studyroom_booking"
set "REDIS_EXE=tools\redis\redis-server.exe"
set "REDIS_CONF=tools\redis\redis.windows.conf"
set "PORTAL_JAR=web\web-portal\target\studyroom-web-portal-1.0-SNAPSHOT.jar"
set "FORCE_BUILD=0"
if /i "%~1"=="build" set "FORCE_BUILD=1"

echo [1/6] 检查运行环境...
where java >nul 2>nul
if errorlevel 1 (
    echo   [错误] 未找到 java，请先安装 JDK 17+ 并配置 PATH。
    exit /b 1
)

rem ---------------------------------------------------------------------
rem  MySQL：本机服务，脚本只检查不创建库；库不存在时提供初始化入口
rem ---------------------------------------------------------------------
echo [2/6] 检查 MySQL（端口 %MYSQL_PORT%）...
call :wait_port %MYSQL_PORT% 5
if not errorlevel 1 goto :after_mysql
echo   MySQL 未在运行，尝试启动本机服务 MySQL...
net start MySQL >nul 2>nul
if not errorlevel 1 goto :mysql_wait
echo   [错误] 无法通过 net start 启动 MySQL 服务，常见原因有两个：
echo          1. 当前不是管理员命令行，启动系统服务需要管理员权限；
echo          2. 本机 MySQL 服务名可能不叫 MySQL，可在 services.msc 中查看实际名称。
echo          请手动启动 MySQL 后重新运行本脚本。
exit /b 1
:mysql_wait
call :wait_port %MYSQL_PORT% 20
if errorlevel 1 (
    echo   [错误] MySQL 服务已启动但端口 %MYSQL_PORT% 未监听，请确认服务状态后重试。
    exit /b 1
)
:after_mysql

rem ---------------------------------------------------------------------
rem  Redis：使用项目内置免安装版
rem ---------------------------------------------------------------------
echo [3/6] 检查 Redis（端口 %REDIS_PORT%）...
call :wait_port %REDIS_PORT% 3
if errorlevel 1 (
    if not exist "%REDIS_EXE%" (
        echo   [错误] 未找到项目内置 Redis：%REDIS_EXE%
        exit /b 1
    )
    echo   启动内置 Redis ...
    start "studyroom-redis(6379)" /MIN "%REDIS_EXE%" "%REDIS_CONF%"
    call :wait_port %REDIS_PORT% 10
    if errorlevel 1 (
        echo   [错误] Redis 启动失败，请查看 studyroom-redis 窗口。
        exit /b 1
    )
)

rem ---------------------------------------------------------------------
rem  数据库初始化（仅当库不存在时询问，避免误删已有数据）
rem ---------------------------------------------------------------------
echo [4/6] 检查数据库 %DB_NAME% ...
where mysql >nul 2>nul
if errorlevel 1 (
    echo   [提示] 未找到 mysql 客户端，跳过数据库自动初始化。
    goto :after_db
)
mysql -h 127.0.0.1 -P %MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PWD% -e "use %DB_NAME%" >nul 2>nul
if not errorlevel 1 goto :after_db
echo   数据库 %DB_NAME% 不存在。
set "INIT_ANSWER=N"
set /p "INIT_ANSWER=   是否现在初始化（执行 sql\studyroom_booking.sql 与 sql\studyroom_init_data.sql）？[y/N] "
if /i not "!INIT_ANSWER!"=="Y" (
    echo   [错误] 已跳过初始化，请先手动导入 sql 目录下的脚本再运行本脚本。
    exit /b 1
)
echo   导入建表脚本 ...
mysql -h 127.0.0.1 -P %MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PWD% --default-character-set=utf8mb4 < sql\studyroom_booking.sql
if errorlevel 1 (
    echo   [错误] 建表脚本执行失败，请检查账号密码（脚本默认 root/1234）。
    exit /b 1
)
echo   导入初始化数据 ...
mysql -h 127.0.0.1 -P %MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PWD% --default-character-set=utf8mb4 < sql\studyroom_init_data.sql
if errorlevel 1 (
    echo   [错误] 初始化数据脚本执行失败。
    exit /b 1
)
:after_db

rem ---------------------------------------------------------------------
rem  构建：jar 缺失或指定 build 时执行
rem ---------------------------------------------------------------------
echo [5/6] 检查门户应用 ...
if exist "%PORTAL_JAR%" if not "%FORCE_BUILD%"=="1" goto :after_build
echo   构建统一门户 jar（首次构建或指定了 build 参数）...
call mvn -q clean package -DskipTests
if errorlevel 1 (
    echo   [错误] 构建失败，请检查 Maven 输出。
    exit /b 1
)
:after_build
if not exist "%PORTAL_JAR%" (
    echo   [错误] 未找到 %PORTAL_JAR%，请先执行：一键启动.bat build
    exit /b 1
)

rem ---------------------------------------------------------------------
rem  启动统一门户并打开浏览器
rem ---------------------------------------------------------------------
echo [6/6] 检查端口 %PORTAL_PORT% ...
rem 已被本应用占用：只打开登录页，不重复启动第二个实例
powershell -NoProfile -Command "try{$r=Invoke-WebRequest -UseBasicParsing -TimeoutSec 2 'http://localhost:%PORTAL_PORT%/login';if($r.StatusCode -eq 200 -and $r.Content -match 'portal.css'){exit 0}}catch{exit 1}" >nul 2>nul
if not errorlevel 1 (
    echo   检测到统一门户已在运行，直接打开登录页。
    start "" http://localhost:%PORTAL_PORT%/login
    exit /b 0
)
rem 端口被其他程序占用：明确提示并退出，不打开浏览器
powershell -NoProfile -Command "try{$c=New-Object Net.Sockets.TcpClient('127.0.0.1',%PORTAL_PORT%);$c.Close();exit 0}catch{exit 1}" >nul 2>nul
if not errorlevel 1 (
    echo   [错误] 端口 %PORTAL_PORT% 已被其他程序占用，请先停止该程序，或修改本脚本头部的 PORTAL_PORT。
    pause
    exit /b 1
)
rem 延迟打开浏览器：仅当登录页真正返回 200 时才打开；应用启动失败则不打开任何页面
start "" /MIN powershell -NoProfile -Command "for($i=0;$i -lt 90;$i++){try{$r=Invoke-WebRequest -UseBasicParsing -TimeoutSec 2 'http://localhost:%PORTAL_PORT%/login';if($r.StatusCode -eq 200 -and $r.Content -match 'portal.css'){Start-Process 'http://localhost:%PORTAL_PORT%/login';exit 0}}catch{Start-Sleep 2}}"
echo   应用启动中，浏览器将在登录页就绪后自动打开（最多等待 3 分钟）...
java -jar "%PORTAL_JAR%"
echo.
echo   统一门户已退出。若为异常退出，请向上翻阅错误信息。
pause
goto :eof

rem ---------------------------------------------------------------------
rem  等待端口就绪：wait_port 端口 最长等待秒数
rem ---------------------------------------------------------------------
:wait_port
setlocal
set /a tries=%2
:wait_loop
powershell -NoProfile -Command "try{$c=New-Object Net.Sockets.TcpClient('127.0.0.1',%1);$c.Close();exit 0}catch{exit 1}" >nul 2>nul
if not errorlevel 1 ( endlocal & exit /b 0 )
set /a tries-=1
if %tries% leq 0 ( endlocal & exit /b 1 )
ping -n 2 127.0.0.1 >nul
goto :wait_loop
