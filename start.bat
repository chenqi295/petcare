@echo off
echo ========================================
echo   PetCare Platform - Windows 启动脚本
echo ========================================
echo.

REM 检查Java是否安装
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Java环境，请先安装JDK 21或更高版本
    pause
    exit /b 1
)

echo [1/3] 检查Java环境...
java -version
echo.

REM 检查MySQL是否运行
echo [2/3] 请确保MySQL服务已启动
echo       数据库: pet_care
echo       用户名: root
echo       密码: root
echo.
echo 如果数据库配置不同，请修改 src\main\resources\application.yml
echo.
pause

REM 启动应用
echo [3/3] 正在启动PetCare平台...
echo.
mvn spring-boot:run

pause
