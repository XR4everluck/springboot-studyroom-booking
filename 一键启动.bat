@echo off
setlocal enabledelayedexpansion
chcp 936 >nul
cd /d "%~dp0"

rem =====================================================================
rem  上岸自习室预订系统 一键启动脚本
rem  应用形态：单端口统一应用(8080)：/ 统一入口 + /console 管理端 + /h5 用户端 + 门户(8090 可选)
rem  用法：
rem    一键启动.bat              检查环境/按需构建，启动单端口统一应用（打开管理端与用户端页面）
rem    一键启动.bat h5|console   同上，浏览器只打开用户端或管理端页面
rem    一键启动.bat restart      先停止应用（保留 Redis）再启动
rem    一键启动.bat build        先执行 mvn clean package -DskipTests 再启动
rem    一键启动.bat portal       额外启动增强管理端门户（Thymeleaf，端口 8090）
rem    stop.bat                  停止统一应用/门户与内置 Redis（stop.bat app 保留 Redis）
rem  行为：启动成功后自动打开管理端(/console/)与用户端(/h5/)两个页面；应用已在运行时跳过启动仅打开页面
rem  依赖：JDK 17+、Maven、MySQL 8（本机服务名 MySQL）、项目内置 Redis（tools\redis）
rem  前端：首次使用需构建两个 Vue 前端并把 dist 拷入 web\web-uni\src\main\resources\uni-static（见 README）
rem  注意：本文件必须以 GBK(cp936) 保存，另存为 UTF-8 会导致 cmd 解析中文错位。
rem =====================================================================

set "UNI_PORT=8080"
set "PORTAL_PORT=8090"
set "REDIS_PORT=6379"
set "MYSQL_PORT=3306"
set "MYSQL_USER=root"
set "MYSQL_PWD=1234"
set "DB_NAME=studyroom_booking"
set "REDIS_EXE=tools\redis\redis-server.exe"
set "REDIS_CONF=tools\redis\redis.windows.conf"
set "UNI_JAR=web\web-uni\target\studyroom-web-uni-1.0-SNAPSHOT.jar"
set "PORTAL_JAR=web\web-portal\target\studyroom-web-portal-1.0-SNAPSHOT.jar"
set "UNI_STATIC=web\web-uni\src\main\resources\uni-static\index.html"
set "FORCE_BUILD=0"
set "START_PORTAL=0"
set "OPEN_MODE=both"
set "DO_RESTART=0"
if /i "%~1"=="build" set "FORCE_BUILD=1"
if /i "%~1"=="portal" set "START_PORTAL=1"
if /i "%~2"=="portal" set "START_PORTAL=1"
if /i "%~1"=="h5" set "OPEN_MODE=h5"
if /i "%~1"=="console" set "OPEN_MODE=console"
if /i "%~1"=="restart" set "DO_RESTART=1"
if /i "%~2"=="h5" set "OPEN_MODE=h5"
if /i "%~2"=="console" set "OPEN_MODE=console"
if /i "%~2"=="restart" set "DO_RESTART=1"
if "%DO_RESTART%"=="1" (
    echo   restart 模式：先停止应用（保留 Redis）...
    call stop.bat app nopause
)

echo [1/7] 检查运行环境...
where java >nul 2>nul
if errorlevel 1 (
    echo   [错误] 未找到 java，请先安装 JDK 17+ 并配置 PATH。
    exit /b 1
)

rem ---------------------------------------------------------------------
rem  MySQL：本机服务，脚本只检查不创建库；库不存在时提供初始化入口
rem ---------------------------------------------------------------------
echo [2/7] 检查 MySQL（端口 %MYSQL_PORT%）...
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
echo [3/7] 检查 Redis（端口 %REDIS_PORT%）...
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
echo [4/7] 检查数据库 %DB_NAME% ...
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
rem  前端静态资源：两个 Vue 需先构建并把 dist 内容拷入对应 static 根
rem ---------------------------------------------------------------------
echo [5/7] 检查前端构建产物 ...
if exist "%UNI_STATIC%" goto :after_static
echo   [提示] 未检测到统一入口页 %UNI_STATIC%，
echo          请先按 README「前端」一节构建两个 Vue 并把 dist 拷入 uni-static
echo          （console / h5 子目录），否则打包出的 jar 无法展示页面。本次仍将启动。
:after_static

rem ---------------------------------------------------------------------
rem  构建：jar 缺失或指定 build 时执行
rem ---------------------------------------------------------------------
echo [6/7] 检查应用 jar ...
if exist "%UNI_JAR%" if not "%FORCE_BUILD%"=="1" goto :after_build
echo   构建应用（首次构建或指定了 build 参数）...
call mvn -q clean package -DskipTests
if errorlevel 1 (
    echo   [错误] 构建失败，请检查 Maven 输出。
    exit /b 1
)
:after_build
if not exist "%UNI_JAR%" (
    echo   [错误] 未找到 %UNI_JAR%，请先执行：一键启动.bat build
    exit /b 1
)

rem ---------------------------------------------------------------------
rem  启动：单端口统一应用(8080)，可选门户(8090)
rem ---------------------------------------------------------------------
echo [7/7] 启动应用 ...
rem 端口已监听则视为应用已在运行，跳过启动，避免重复启动端口冲突报错
call :wait_port %UNI_PORT% 1
if not errorlevel 1 goto :open_browser
echo   启动统一应用（端口 %UNI_PORT%，内含管理端 /console 与用户端 /h5）...
start "studyroom-uni(8080)" java -jar "%UNI_JAR%"
:open_browser
rem 浏览器延迟到端口就绪后再打开（独立进程轮询，不阻塞应用启动），按参数决定打开哪些页面
if "%OPEN_MODE%"=="h5" (
    start "" /MIN powershell -NoProfile -Command "for($i=0;$i -lt 60;$i++){try{$r=Invoke-WebRequest -UseBasicParsing -TimeoutSec 2 'http://localhost:%UNI_PORT%/console/';if($r.StatusCode -eq 200){Start-Process 'http://localhost:%UNI_PORT%/h5/';exit 0}}catch{Start-Sleep 2}}"
) else if "%OPEN_MODE%"=="console" (
    start "" /MIN powershell -NoProfile -Command "for($i=0;$i -lt 60;$i++){try{$r=Invoke-WebRequest -UseBasicParsing -TimeoutSec 2 'http://localhost:%UNI_PORT%/console/';if($r.StatusCode -eq 200){Start-Process 'http://localhost:%UNI_PORT%/console/';exit 0}}catch{Start-Sleep 2}}"
) else (
    start "" /MIN powershell -NoProfile -Command "for($i=0;$i -lt 60;$i++){try{$r=Invoke-WebRequest -UseBasicParsing -TimeoutSec 2 'http://localhost:%UNI_PORT%/console/';if($r.StatusCode -eq 200){Start-Process 'http://localhost:%UNI_PORT%/console/';Start-Process 'http://localhost:%UNI_PORT%/h5/';exit 0}}catch{Start-Sleep 2}}"
)
if "%START_PORTAL%"=="1" (
    if exist "%PORTAL_JAR%" (
        echo   启动增强管理端门户（端口 %PORTAL_PORT%）...
        start "studyroom-portal(8090)" java -jar "%PORTAL_JAR%"
    ) else (
        echo   [提示] 未找到门户 jar，跳过门户启动（运行 一键启动.bat build 可构建）。
    )
)
echo.
echo   启动完成：
echo     统一入口 http://localhost:%UNI_PORT%/          （入口卡片选择两端，接口文档 /doc.html）
echo     管理端   http://localhost:%UNI_PORT%/console/  （admin / 123456，账号密码登录）
echo     用户端   http://localhost:%UNI_PORT%/h5/       （demo001 / 123456，支持自助注册）
if "%START_PORTAL%"=="1" echo     增强门户 http://localhost:%PORTAL_PORT%/login  （运营台/报表/大屏）
echo   关闭对应命令行窗口即可停止各应用。
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
