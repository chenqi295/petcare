# PetCare 项目部署清单

## 📦 项目文件说明

### 核心文件（必须）
- ✅ `src/` - 后端源代码
- ✅ `pom.xml` - Maven配置
- ✅ `frontend/` - 前端源代码
- ✅ `sql/init_database.sql` - 数据库初始化脚本
- ✅ `.gitignore` - Git忽略配置

### 部署文件（生产环境需要）
- ✅ `nginx.conf` - Nginx配置文件
- ✅ `deploy.sh` - 自动化部署脚本
- ✅ `DEPLOYMENT.md` - 详细部署文档
- ✅ `README.md` - 项目说明文档

### 开发工具（可选）
-  `.idea/` - IntelliJ IDEA配置
- 📁 `.mvn/` - Maven wrapper
- 📄 `start.bat` - Windows启动脚本
- 📄 `start.sh` - Linux启动脚本

### 数据目录（运行时生成）
- 📁 `target/` - 编译输出目录
-  `uploads/` - 上传文件存储

---

## 🚀 快速部署步骤

### 1️ 本地准备
```powershell
cd D:\Java\Demo\PetCare
mvn clean package -DskipTests
Compress-Archive -Path src, pom.xml, sql, frontend, nginx.conf, deploy.sh, .gitignore -DestinationPath PetCare-deploy.zip
scp PetCare-deploy.zip root@8.148.80.252:/root/
```

### 2️⃣ 服务器环境安装
参考 [DEPLOYMENT.md](DEPLOYMENT.md) 中的"步骤一：安装环境"

### 3️⃣ 一键部署
```bash
cd /root/PetCare
chmod +x deploy.sh
./deploy.sh
```

---

## ✅ 验证部署

访问以下地址确认部署成功：
- [http://8.148.80.252](http://8.148.80.252) - 前端页面
- [http://8.148.80.252/api/test](http://8.148.80.252/api/test) - 后端API测试

默认管理员账号：
- 用户名: admin
- 密码: admin123

---

## 🔧 常用维护命令

```bash
# 查看日志
tail -f /root/PetCare/app.log

# 重启服务
pkill -f pet-care-platform && cd /root/PetCare && nohup java -jar target/pet-care-platform-1.0.0.jar > app.log 2>&1 &

# 重启Nginx
systemctl restart nginx
```

---

##  相关文档

- [DEPLOYMENT.md](DEPLOYMENT.md) - 完整部署指南
- [README.md](README.md) - 项目文档和API说明
