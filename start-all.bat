@echo off
setlocal enabledelayedexpansion
chcp 936 >nul
cd /d "%~dp0"

rem =====================================================================
rem  上岸自习室预订系统 一键启动脚本
rem  用法：
rem    start-all.bat            使用已有 jar 直接启动（缺失时自动构建）
rem    start-all.bat build      先执行 mvn clean package 再启动
rem  依赖：JDK 17+、Maven、MySQL 8（本机服务名 MySQL）、项目内置 Redis（tools\redis）
rem  注意：本文件必须以 GBK(cp936) 保存，另存为 UTF-8 会导致 cmd 解析中文错位。
rem        echo 与 rem 注释的文案里不要写重定向符号，cmd 仍会解析并可能生成垃圾文件。
rem =====================================================================

set "ADMIN_PORT=8080"
set "APP_PORT=8081"
set "REDIS_PORT=6379"
set "MYSQL_PORT=3306"
set "MYSQL_USER=root"
set "MYSQL_PWD=1234"
set "DB_NAME=studyroom_booking"
set "REDIS_EXE=tools\redis\redis-server.exe"
set "REDIS_CONF=tools\redis\redis.windows.conf"
set "ADMIN_JAR=web\web-admin\target\studyroom-web-admin-1.0-SNAPSHOT.jar"
set "APP_JAR=web\web-app\target\studyroom-web-app-1.0-SNAPSHOT.jar"
set "ADMIN_STATIC=web\web-admin\src\main\resources\static\index.html"
set "APP_STATIC=web\web-app\src\main\resources\static\index.html"
set "FORCE_BUILD=0"
if /i "%~1"=="build" set "FORCE_BUILD=1"

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
rem  前端静态资源：两端 Vue 需先构建并把 dist 内容拷进 Spring 的 static 目录
rem ---------------------------------------------------------------------
echo [5/7] 检查前端静态资源 ...
if exist "%ADMIN_STATIC%" if exist "%APP_STATIC%" goto :after_static
echo   [警告] 未检测到已内嵌的前端页面，后端虽能启动，但浏览器访问会白屏或 404：
if not exist "%ADMIN_STATIC%" echo          缺失 %ADMIN_STATIC%
if not exist "%APP_STATIC%" echo          缺失 %APP_STATIC%
echo   请分别进入 web\web-admin\src\main\resources\static\studyroomAdmin 与
echo          web\web-app\src\main\resources\static\studyroomH5 执行
echo          npm install 与 npm run build，再把各自 dist 里的内容拷到上级 static 目录后重新打包。
pause
exit /b 1
:after_static

rem ---------------------------------------------------------------------
rem  构建：缺失 jar 或传入 build 参数时执行
rem ---------------------------------------------------------------------
echo [6/7] 准备后端jar ...
if "%FORCE_BUILD%"=="1" goto :build
if exist "%ADMIN_JAR%" if exist "%APP_JAR%" goto :built
:build
where mvn >nul 2>nul
if errorlevel 1 (
    echo   [错误] 未找到 mvn，无法构建，请安装 Maven 后重试。
    exit /b 1
)
rem target 下的 jar 被运行中的进程占用时 mvn clean 会失败，先释放 8080/8081
call :free_port %ADMIN_PORT%
call :free_port %APP_PORT%
echo   执行 mvn clean package -DskipTests（首次构建约需数分钟）...
call mvn -q clean package -DskipTests
if errorlevel 1 (
    echo   [错误] 构建失败，请查看上方 Maven 输出。
    exit /b 1
)
:built

rem ---------------------------------------------------------------------
rem  启动两端服务（各自独立窗口，日志直接显示在窗口内）
rem ---------------------------------------------------------------------
echo [7/7] 启动后台管理端与用户用户端 ...
start "studyroom-admin(8080)" cmd /k "java -jar %ADMIN_JAR%"
start "studyroom-app(8081)" cmd /k "java -jar %APP_JAR%"

call :wait_port %ADMIN_PORT% 90
if errorlevel 1 echo   [警告] 管理端 8080 未就绪，请查看 studyroom-admin 窗口。
call :wait_port %APP_PORT% 90
if errorlevel 1 echo   [警告] 用户端 8081 未就绪，请查看 studyroom-app 窗口。

start "" http://localhost:%ADMIN_PORT%/
start "" http://localhost:%APP_PORT%/

echo.
echo =====================================================================
echo   启动完成
echo     管理后台： http://localhost:%ADMIN_PORT%/   （admin / 123456）
echo     用户端：   http://localhost:%APP_PORT%/     （账号 demo001，密码 123456）
echo   说明：用户端为账号密码登录，支持自助注册；测试账号见 sql\studyroom_init_data.sql。
echo   停止服务请运行 stop-all.bat
echo =====================================================================
goto :eof

rem ---------------------------------------------------------------------
rem  子过程：等待端口被监听，参数 1=端口 2=最长等待秒数
rem ---------------------------------------------------------------------
:wait_port
set "WP_LIMIT=%~2"
set "WP_COUNT=0"
:wait_port_loop
netstat -ano | findstr /R /C:"LISTENING" | findstr /C:":%~1 " >nul 2>nul
if not errorlevel 1 exit /b 0
call :sleep_one
set /a WP_COUNT+=1
if %WP_COUNT% LSS %WP_LIMIT% goto :wait_port_loop
exit /b 1

rem ---------------------------------------------------------------------
rem  子过程：结束占用某端口的进程，仅用于释放被锁定的 jar。
rem  只处理传入的端口，绝不触碰 MySQL(3306) 与 Redis(6379)。
rem ---------------------------------------------------------------------
:free_port
set "KILLED_="
for /f "tokens=5" %%p in ('netstat -ano ^| findstr /R /C:"LISTENING" ^| findstr /C:":%~1 "') do (
    if not defined KILLED_%%p (
        set "KILLED_%%p=1"
        echo   端口 %~1 已被占用，结束进程 PID %%p 以释放 jar 文件锁
        taskkill /f /pid %%p >nul 2>nul
    )
)
call :sleep_one
exit /b 0

rem ---------------------------------------------------------------------
rem  子过程：约等待 1 秒。用 System32 里 ping 的绝对路径，避免 Git 自带的
rem  timeout/ping 先在 PATH 中命中，导致等待失效退化成忙等。
rem ---------------------------------------------------------------------
:sleep_one
%SystemRoot%\System32\ping.exe -n 2 127.0.0.1 1>nul 2>nul
exit /b 0
