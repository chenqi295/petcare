# 宠物服务预约平台 (PetCare Platform)

一个功能完整的宠物服务预约平台，基于 Spring Boot + Vue.js 开发。

## 项目简介

PetCare 是一个专业的宠物服务平台，为用户提供宠物美容、医疗保健、训练教育、寄养等全方位服务的在线预约和管理系统。

## 主要功能

### 用户端
- ✅ 用户注册/登录（JWT认证）
- ✅ 宠物信息管理（添加、编辑、删除）
- ✅ 服务浏览与搜索
- ✅ 在线预约服务
- ✅ 订单管理（支付、取消）
- ✅ 服务评价
- ✅ 个人中心

### 技师端
- ✅ 预约管理（确认、开始、完成服务）
- ✅ 查看个人评价
- ✅ 工作状态管理

### 管理端
- ✅ 服务分类管理
- ✅ 服务项目管理
- ✅ 技师管理
- ✅ 订单管理
- ✅ 用户管理

## 技术栈

### 后端
- Java 21
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0
- JWT (io.jsonwebtoken 0.12.3)
- Hutool 5.8.24
- Lombok

### 前端（待实现）
- Vue 3
- Vite
- Element Plus
- Axios
- Pinia

## 项目结构

```
PetCare/
├── src/
│   ├── main/
│   │   ├── java/org/petcare/
│   │   │   ├── PetCareApplication.java    # 主启动类
│   │   │   ├── common/                     # 通用类
│   │   │   │   └── Result.java            # 统一响应
│   │   │   ├── config/                     # 配置类
│   │   │   │   ├── CorsConfig.java        # 跨域配置
│   │   │   │   ├── MyMetaObjectHandler.java  # 自动填充
│   │   │   │   └── MybatisPlusConfig.java # MyBatis配置
│   │   │   ├── controller/                 # 控制器
│   │   │   │   ├── UserController.java    # 用户接口
│   │   │   │   ├── PetController.java     # 宠物接口
│   │   │   │   ├── ServiceController.java # 服务接口
│   │   │   │   ├── AppointmentController.java # 预约接口
│   │   │   │   ├── OrderController.java   # 订单接口
│   │   │   │   ├── ReviewController.java  # 评价接口
│   │   │   │   └── TestController.java    # 测试接口
│   │   │   ├── entity/                     # 实体类
│   │   │   │   ├── User.java              # 用户
│   │   │   │   ├── Pet.java               # 宠物
│   │   │   │   ├── ServiceCategory.java   # 服务分类
│   │   │   │   ├── ServiceItem.java       # 服务项目
│   │   │   │   ├── Technician.java        # 技师
│   │   │   │   ├── Appointment.java       # 预约
│   │   │   │   ├── Order.java             # 订单
│   │   │   │   └── Review.java            # 评价
│   │   │   ├── exception/                  # 异常处理
│   │   │   │   ├── BusinessException.java # 业务异常
│   │   │   │   └── GlobalExceptionHandler.java # 全局异常处理
│   │   │   ├── mapper/                     # Mapper接口
│   │   │   ├── service/                    # 服务层
│   │   │   └── util/                       # 工具类
│   │   │       └── JwtUtil.java           # JWT工具
│   │   └── resources/
│   │       └── application.yml            # 配置文件
├── sql/
│   └── schema.sql                         # 数据库脚本
└── pom.xml                                # Maven配置
```

## 快速开始

### 环境要求
- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Node.js 18+ (前端开发)

### 数据库配置

1. 创建数据库并导入数据：
```bash
mysql -u root -p < sql/schema.sql
```

2. 修改配置文件 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pet_care?useUnicode=true&characterEncoding=utf8
    username: your_username
    password: your_password
```

### 运行后端

```bash
# 编译项目
mvn clean package

# 运行应用
mvn spring-boot:run

# 或者直接运行jar包
java -jar target/pet-care-platform-1.0.0.jar
```

后端服务将在 http://localhost:8080/api 启动

### API测试

```bash
# 测试接口
curl http://localhost:8080/api/test/hello

# 用户注册
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456","phone":"13800138000"}'

# 用户登录
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

## API文档

### 用户相关
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/info` - 获取用户信息
- `PUT /api/user/update` - 更新用户信息
- `POST /api/user/change-password` - 修改密码

### 宠物相关
- `POST /api/pet/add` - 添加宠物
- `PUT /api/pet/update` - 更新宠物
- `DELETE /api/pet/delete/{id}` - 删除宠物
- `GET /api/pet/{id}` - 获取宠物详情
- `GET /api/pet/my-pets` - 获取我的宠物列表
- `GET /api/pet/page` - 分页查询宠物

### 服务相关
- `GET /api/service/categories` - 获取服务分类
- `GET /api/service/items/{categoryId}` - 获取服务项目
- `GET /api/service/hot` - 获取热门服务
- `GET /api/service/detail/{id}` - 获取服务详情

### 预约相关
- `POST /api/appointment/create` - 创建预约
- `POST /api/appointment/cancel/{id}` - 取消预约
- `GET /api/appointment/my-appointments` - 获取我的预约
- `GET /api/appointment/{id}` - 获取预约详情
- `POST /api/appointment/confirm/{id}` - 确认预约
- `POST /api/appointment/start/{id}` - 开始服务
- `POST /api/appointment/complete/{id}` - 完成服务

### 订单相关
- `GET /api/order/my-orders` - 获取我的订单
- `GET /api/order/{id}` - 获取订单详情
- `POST /api/order/pay/{id}` - 支付订单
- `POST /api/order/cancel/{id}` - 取消订单

### 评价相关
- `POST /api/review/create` - 创建评价
- `GET /api/review/service/{serviceId}` - 获取服务评价
- `GET /api/review/technician/{technicianId}` - 获取技师评价
- `GET /api/review/my-reviews` - 获取我的评价

## 默认账号

管理员账号：
- 用户名: admin
- 密码: admin123

## 数据库设计

### 核心表结构
- `user` - 用户表
- `pet` - 宠物表
- `service_category` - 服务分类表
- `service_item` - 服务项目表
- `technician` - 技师表
- `appointment` - 预约表
- `orders` - 订单表
- `review` - 评价表

详细表结构请查看 `sql/schema.sql`

## 部署说明

### 方式一：自动化脚本部署（推荐）

1. 上传项目到服务器 `/root/PetCare` 目录
2. 执行一键部署脚本：
```bash
cd /root/PetCare
chmod +x deploy.sh
./deploy.sh
```

3. 访问应用：
   - 前端: http://8.148.80.252
   - 后端API: http://8.148.80.252/api

### 方式二：手动部署

#### 环境要求
- Java 21+
- Maven 3.6+
- MySQL 8.0+
- Node.js 18+
- Nginx

#### 1. 安装依赖
```bash
# 安装 Java 21
yum install java-21-openjdk java-21-openjdk-devel -y

# 安装 MySQL
wget https://dev.mysql.com/get/mysql80-community-release-el7-9.noarch.rpm
rpm -ivh mysql80-community-release-el7-9.noarch.rpm
yum install mysql-server -y
systemctl start mysqld
systemctl enable mysqld

# 初始化MySQL密码并创建数据库
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED BY '1234';
CREATE DATABASE pet_care CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;

# 导入数据库
mysql -u root -p1234 pet_care < sql/init_database.sql

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
```

#### 2. 编译后端
```bash
cd /root/PetCare
mvn clean package -DskipTests
```

#### 3. 构建前端
```bash
cd /root/PetCare/frontend
npm install
npm run build
```

#### 4. 配置Nginx
```bash
# 复制配置文件
cp /root/PetCare/nginx.conf /etc/nginx/conf.d/petcare.conf

# 复制前端文件
cp -r dist/* /usr/share/nginx/html/

# 重启Nginx
nginx -t
systemctl restart nginx
```

#### 5. 启动后端
```bash
cd /root/PetCare
nohup java -jar target/pet-care-platform-1.0.0.jar > app.log 2>&1 &

# 查看日志
tail -f app.log
```

#### 6. 开放防火墙端口
```bash
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --reload
```

### 常用运维命令

```bash
# 查看后端日志
tail -f /root/PetCare/app.log

# 重启后端
pkill -f pet-care-platform
cd /root/PetCare
nohup java -jar target/pet-care-platform-1.0.0.jar > app.log 2>&1 &

# 重启Nginx
systemctl restart nginx

# 查看进程
ps aux | grep java

# 检查端口
netstat -tlnp | grep 8080
netstat -tlnp | grep 80
```

## 开发计划

- [ ] 前端Vue项目开发
- [ ] 文件上传功能（宠物照片、服务图片）
- [ ] 消息通知系统
- [ ] 数据统计与分析
- [ ] 技师位置服务
- [ ] 在线聊天功能
- [ ] 优惠券系统
- [ ] 会员积分系统

## 常见问题

### 1. 启动时提示端口被占用
修改 `application.yml` 中的 `server.port` 配置

### 2. 数据库连接失败
- 检查MySQL服务是否启动
- 检查数据库名、用户名、密码是否正确
- 检查数据库权限

### 3. JWT Token验证失败
- 检查请求头是否包含 `Authorization: Bearer {token}`
- 检查Token是否过期

## 许可证

MIT License

## 联系方式

如有问题或建议，请提交Issue或联系开发者。

---

**注意**: 这是一个演示项目，生产环境需要进一步完善安全措施、性能优化和功能增强。
