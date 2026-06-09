# PetCare 项目文件清单

##  项目目录结构

```
PetCare/
├── 📄 README.md                      # 项目主文档（保留）
├── 📄 DEPLOYMENT.md                  # 部署指南（新增）
├──  CHECKLIST_DEPLOY.md            # 部署清单（新增）
├── 📄 pom.xml                        # Maven配置（核心）
├── 📄 .gitignore                     # Git忽略配置（保留）
│
├── 📁 src/                           # 后端源代码（核心）
│   ├── main/
│   │   ├── java/org/petcare/
│   │   │   ├── PetCareApplication.java    # 启动类
│   │   │   ├── common/                    # 通用类
│   │   │   ├── config/                    # 配置类
│   │   │   ├── controller/                # 控制器
│   │   │   ├── entity/                    # 实体类
│   │   │   ├── exception/                 # 异常处理
│   │   │   ├── mapper/                    # Mapper接口
│   │   │   ├── service/                   # 服务层
│   │   │   └── util/                      # 工具类
│   │   └── resources/
│   │       ── application.yml            # 配置文件
│   ── test/                              # 测试代码
│
├── 📁 frontend/                      # 前端源代码（核心）
│   ├── src/
│   │   ├── api/                       # API接口
│   │   ├── components/                # 组件
│   │   ├── router/                    # 路由
│   │   ├── stores/                    # 状态管理
│   │   ├── utils/                     # 工具函数
│   │   ├── views/                     # 页面视图
│   │   ├── App.vue                    # 根组件
│   │   └── main.js                    # 入口文件
│   ├── public/                        # 静态资源
│   ├── index.html                     # HTML模板
│   ├── package.json                   # 依赖配置
│   └── vite.config.js                 # Vite配置
│
├── 📁 sql/                           # 数据库脚本（核心）
│   └── init_database.sql              # 初始化脚本
│
├── 📁 uploads/                       # 上传文件（运行时生成）
│   ├── avatars/                       # 头像
│   └── pets/                          # 宠物照片
│
├── 📁 target/                        # 编译输出（自动生成）
│   └── pet-care-platform-1.0.0.jar    # 打包后的JAR包
│
├── 📄 nginx.conf                     # Nginx配置（生产环境需要）
├── 📄 deploy.sh                      # 自动化部署脚本（生产环境需要）
── 📄 start.bat                      # Windows启动脚本（开发用）
├── 📄 start.sh                       # Linux启动脚本（开发用）
│
├── 📁 .idea/                         # IDEA配置（可选，可删除）
└──  .mvn/                          # Maven wrapper（可选）
```

---

## ✅ 核心文件说明

### 必须保留的文件

| 文件/目录 | 说明 | 用途 |
|---------|------|------|
| `src/` | 后端源代码 | 核心业务逻辑 |
| `pom.xml` | Maven配置 | 项目依赖管理 |
| `frontend/` | 前端源代码 | 用户界面 |
| `sql/init_database.sql` | 数据库脚本 | 表结构和初始数据 |
| `.gitignore` | Git配置 | 版本控制 |
| `README.md` | 项目文档 | 使用说明 |

### 生产环境需要的文件

| 文件 | 说明 | 用途 |
|------|------|------|
| `nginx.conf` | Nginx配置 | 反向代理和静态文件服务 |
| `deploy.sh` | 部署脚本 | 一键部署到服务器 |
| `DEPLOYMENT.md` | 部署文档 | 详细部署步骤 |
| `CHECKLIST_DEPLOY.md` | 部署清单 | 快速参考 |

### 开发辅助文件（可选）

| 文件/目录 | 说明 | 是否必需 |
|----------|------|----------|
| `.idea/` | IDEA配置 | ❌ 可选 |
| `.mvn/` | Maven wrapper |  可选 |
| `start.bat` | Windows启动 | ⚠️ 开发用 |
| `start.sh` | Linux启动 | ️ 开发用 |

### 运行时生成的目录

| 目录 | 说明 | 注意 |
|------|------|------|
| `target/` | 编译输出 | 不要提交Git |
| `uploads/` | 上传文件 | 需要备份 |

---

## 🗑️ 已删除的文件

以下文件已被删除，不影响项目功能：

- ❌ `CHECKLIST.md` - 检查清单
- ❌ `DELIVERY.md` - 交付文档
- ❌ `FEATURES_COMPLETE.md` - 功能完成说明
- ❌ `PROJECT_SUMMARY.md` - 项目总结
- ❌ `QUICK_START.md` - 快速启动指南
- ❌ `REGISTRATION_UPDATE.md` - 注册更新说明
- ❌ `REVIEW_FEATURE.md` - 评价功能说明
- ❌ `TECHNICIAN_API_COMPLETE.md` - 技师API完成说明
- ❌ `黑盒测试文档.md` - 测试文档
- ❌ `启动指南.md` - 启动指南
- ❌ `fix-data.html` - 数据修复工具
- ❌ `fix-order.html` - 订单修复工具
- ❌ `application-prod-example.yml` - 生产环境示例
- ❌ `Dockerfile.backend` - Docker构建文件
-  `docker-compose.yml` - Docker编排文件
- ❌ `API_TEST.http` - API测试文件
- ❌ `package-lock.json` (根目录) - 空锁文件
- ❌ `nginx.conf.example` - Nginx示例配置

---

## 📦 上传到服务器的文件

使用以下命令打包并上传：

```powershell
# 在本地Windows PowerShell执行
cd D:\Java\Demo\PetCare

# 压缩必要文件
Compress-Archive -Path src, pom.xml, sql, frontend, nginx.conf, deploy.sh, .gitignore, README.md, DEPLOYMENT.md, CHECKLIST_DEPLOY.md -DestinationPath PetCare-deploy.zip

# 上传到服务器
scp PetCare-deploy.zip root@8.148.80.252:/root/
```

---

## 🎯 最小化部署清单

如果只需要运行项目，最少需要以下文件：

```
PetCare/
├── src/
├── pom.xml
├── frontend/
├── sql/init_database.sql
── nginx.conf
└── deploy.sh
```

---

## 🔍 文件大小估算

| 目录/文件 | 大小（约） | 说明 |
|----------|-----------|------|
| `src/` | 500 KB | 后端代码 |
| `frontend/` | 2 MB | 前端代码+node_modules |
| `sql/` | 15 KB | 数据库脚本 |
| `target/` | 30 MB | JAR包（编译后） |
| `uploads/` | 可变 | 用户上传文件 |
| **总计** | ~35 MB | 不含node_modules |

---

## 💡 建议

1. **定期清理** `target/` 目录，避免占用过多空间
2. **备份** `uploads/` 目录中的用户上传文件
3. **备份** 数据库，防止数据丢失
4. **监控** 日志文件大小，定期清理

---

## 📞 相关文档

- [README.md](README.md) - 完整项目文档
- [DEPLOYMENT.md](DEPLOYMENT.md) - 详细部署指南
- [CHECKLIST_DEPLOY.md](CHECKLIST_DEPLOY.md) - 快速部署清单
