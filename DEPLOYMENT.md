# PetCare 项目阿里云部署指南

## 📋 快速部署（3步完成）

### 前提条件
- 已重置阿里云服务器系统盘（CentOS 7/8）
- 服务器IP: 8.148.80.252
- MySQL密码: 1234

---

## 🚀 步骤一：安装环境（在服务器上执行）

```bash
# 更新系统
yum update -y

# 安装 Java 21
yum install java-21-openjdk java-21-openjdk-devel -y

# 安装 MySQL 8.0
wget https://dev.mysql.com/get/mysql80-community-release-el7-9.noarch.rpm
rpm -ivh mysql80-community-release-el7-9.noarch.rpm
yum install mysql-server -y
systemctl start mysqld
systemctl enable mysqld

# 获取临时密码并修改
grep 'temporary password' /var/log/mysqld.log
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED BY '1234';
CREATE DATABASE pet_care CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;

# 安装 Nginx
yum install nginx -y
systemctl start nginx
systemctl enable nginx

# 安装 Node.js
curl -fsSL https://rpm.nodesource.com/pub_20.x/nodesource/repo/el/7/x86_64/nodesource-repo-20-1.el7.x86_64.rpm | rpm -i -
yum install nodejs -y

# 安装 Maven
wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
tar -zxvf apache-maven-3.9.6-bin.tar.gz
mv apache-maven-3.9.6 /opt/
export PATH=/opt/apache-maven-3.9.6/bin:$PATH
echo 'export PATH=/opt/apache-maven-3.9.6/bin:$PATH' >> ~/.bashrc
```

---

## 📤 步骤二：上传项目（在本地Windows PowerShell执行）

```powershell
# 进入项目目录
cd D:\Java\Demo\PetCare

# 打包后端
mvn clean package -DskipTests

# 压缩项目文件
Compress-Archive -Path src, pom.xml, sql, frontend, nginx.conf, deploy.sh, .gitignore -DestinationPath PetCare-deploy.zip

# 上传到服务器
scp PetCare-deploy.zip root@8.148.80.252:/root/
```

---

## 🔧 步骤三：部署项目（在服务器上执行）

```bash
# 解压项目
cd /root
unzip PetCare-deploy.zip
cd PetCare

# 赋予脚本执行权限
chmod +x deploy.sh

# 执行一键部署
./deploy.sh
```

---

## ✅ 访问应用

部署完成后，浏览器访问：
- **前端页面**: http://8.148.80.252
- **后端API**: http://8.148.80.252/api/test

---

## 🔑 默认账号

**管理员账号**：
- 用户名: admin
- 密码: admin123

---

## ️ 常用运维命令

```bash
# 查看后端日志
tail -f /root/PetCare/app.log

# 重启后端服务
pkill -f pet-care-platform
cd /root/PetCare
nohup java -jar target/pet-care-platform-1.0.0.jar > app.log 2>&1 &

# 重启Nginx
systemctl restart nginx

# 检查端口占用
netstat -tlnp | grep 8080
netstat -tlnp | grep 80

# 查看进程
ps aux | grep java
```

---

## ⚠️ 注意事项

1. **防火墙配置**：确保阿里云安全组开放了80端口
2. **数据库密码**：如果MySQL密码不是1234，需要修改 `application.yml`
3. **日志监控**：定期清理日志文件，避免磁盘占满
4. **备份策略**：定期备份数据库和上传的文件

---

## 🆘 常见问题

### 1. 后端启动失败
```bash
# 查看错误日志
tail -f /root/PetCare/app.log

# 检查端口是否被占用
netstat -tlnp | grep 8080

# 检查Java版本
java -version
```

### 2. 前端无法访问
```bash
# 检查Nginx状态
systemctl status nginx

# 检查Nginx配置
nginx -t

# 查看Nginx错误日志
tail -f /var/log/nginx/petcare_error.log
```

### 3. API请求失败
```bash
# 测试后端接口
curl http://localhost:8080/api/test

# 检查Nginx反向代理配置
cat /etc/nginx/conf.d/petcare.conf
```

---

## 📞 技术支持

如遇到问题，请查看：
- README.md - 完整文档
- 后端日志: `/root/PetCare/app.log`
- Nginx日志: `/var/log/nginx/petcare_error.log`
