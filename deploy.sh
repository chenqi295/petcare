#!/bin/bash
# PetCare 项目自动化部署脚本

echo "=========================================="
echo "PetCare 项目部署脚本"
echo "=========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 错误处理
set -e
trap 'echo -e "${RED}❌ 部署失败！${NC}"; exit 1' ERR

# 项目目录
PROJECT_DIR="/root/PetCare"

echo ""
echo "步骤 1: 检查环境..."
echo "------------------------------------------"

# 检查Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java 未安装，请先安装 Java 21${NC}"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo -e "${RED}❌ Java 版本需要 21 或更高，当前版本: $JAVA_VERSION${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Java 版本: $JAVA_VERSION${NC}"

# 检查MySQL
if ! systemctl is-active --quiet mysqld; then
    echo -e "${YELLOW}⚠️ MySQL 未运行，尝试启动...${NC}"
    systemctl start mysqld
fi
echo -e "${GREEN}✅ MySQL 已运行${NC}"

# 检查Nginx
if ! command -v nginx &> /dev/null; then
    echo -e "${RED}❌ Nginx 未安装${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Nginx 已安装${NC}"

# 检查Node.js
if ! command -v node &> /dev/null; then
    echo -e "${RED}❌ Node.js 未安装${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Node.js 版本: $(node -v)${NC}"

# 检查Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven 未安装${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Maven 已安装${NC}"

echo ""
echo "步骤 2: 停止旧服务..."
echo "------------------------------------------"
pkill -f "pet-care-platform" || true
echo -e "${GREEN}✅ 旧服务已停止${NC}"

echo ""
echo "步骤 3: 编译后端..."
echo "------------------------------------------"
cd $PROJECT_DIR
mvn clean package -DskipTests
echo -e "${GREEN}✅ 后端编译完成${NC}"

echo ""
echo "步骤 4: 初始化数据库..."
echo "------------------------------------------"
mysql -u root -p1234 pet_care < sql/init_database.sql 2>/dev/null || {
    echo -e "${YELLOW}⚠️ 数据库可能已存在，跳过初始化${NC}"
}
echo -e "${GREEN}✅ 数据库准备完成${NC}"

echo ""
echo "步骤 5: 构建前端..."
echo "------------------------------------------"
cd $PROJECT_DIR/frontend
npm install
npm run build
echo -e "${GREEN}✅ 前端构建完成${NC}"

echo ""
echo "步骤 6: 配置Nginx..."
echo "------------------------------------------"
cp $PROJECT_DIR/nginx.conf /etc/nginx/conf.d/petcare.conf
mkdir -p /usr/share/nginx/html
cp -r dist/* /usr/share/nginx/html/
nginx -t
systemctl restart nginx
echo -e "${GREEN}✅ Nginx 配置完成${NC}"

echo ""
echo "步骤 7: 启动后端服务..."
echo "------------------------------------------"
cd $PROJECT_DIR
nohup java -jar target/pet-care-platform-1.0.0.jar > app.log 2>&1 &
sleep 5

# 检查是否启动成功
if curl -s http://localhost:8080/api/test > /dev/null 2>&1; then
    echo -e "${GREEN}✅ 后端服务启动成功${NC}"
else
    echo -e "${RED}❌ 后端服务启动失败，请查看日志: tail -f app.log${NC}"
    exit 1
fi

echo ""
echo "=========================================="
echo -e "${GREEN}🎉 部署完成！${NC}"
echo "=========================================="
echo ""
echo "访问地址:"
echo "  前端: http://8.148.80.252"
echo "  后端API: http://8.148.80.252/api"
echo ""
echo "常用命令:"
echo "  查看后端日志: tail -f $PROJECT_DIR/app.log"
echo "  重启后端: pkill -f pet-care-platform && cd $PROJECT_DIR && nohup java -jar target/pet-care-platform-1.0.0.jar > app.log 2>&1 &"
echo "  重启Nginx: systemctl restart nginx"
echo ""
