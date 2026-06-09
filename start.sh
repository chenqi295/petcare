#!/bin/bash

echo "========================================"
echo "   PetCare Platform - Linux/Mac 启动脚本"
echo "========================================"
echo ""

# 检查Java是否安装
if ! command -v java &> /dev/null; then
    echo "[错误] 未检测到Java环境，请先安装JDK 21或更高版本"
    exit 1
fi

echo "[1/3] 检查Java环境..."
java -version
echo ""

# 检查MySQL是否运行
echo "[2/3] 请确保MySQL服务已启动"
echo "      数据库: pet_care"
echo "      用户名: root"
echo "      密码: root"
echo ""
echo "如果数据库配置不同，请修改 src/main/resources/application.yml"
echo ""
read -p "按回车键继续..."

# 启动应用
echo "[3/3] 正在启动PetCare平台..."
echo ""
mvn spring-boot:run
