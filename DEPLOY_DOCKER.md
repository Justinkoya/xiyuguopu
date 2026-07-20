# 西域果铺全 Docker 部署

本指南用于清空后的新服务器部署。Compose 会启动 `mariadb`、`server`、`nginx` 三个容器，首次启动自动导入 `sql/init.sql`。

## 1. 安装运行环境

阿里云 Linux 3 推荐使用 Podman：

```bash
dnf install -y git podman podman-docker podman-compose
podman version
podman-compose --version
```

阿里云安全组放行：

```text
TCP 80   0.0.0.0/0
TCP 22   你的登录来源 IP，或临时 0.0.0.0/0
```

不要放行 `3306`。

## 2. 拉取代码

```bash
cd /opt
git clone -b codex/server-ip-deploy https://github.com/Justinkoya/xiyuguopu.git xiyuguopu
cd /opt/xiyuguopu
```

## 3. 创建环境变量

```bash
cp .env.example .env
vi .env
```

必须修改：

```dotenv
MYSQL_ROOT_PASSWORD=数据库root强密码
MYSQL_PASSWORD=应用数据库强密码
XIYU_JWT_SECRET=至少32字符随机密钥
APP_CORS_ALLOWED_ORIGINS=http://47.109.93.162
NGINX_PORT=80
```

生成 JWT 密钥：

```bash
openssl rand -base64 48
```

## 4. 启动

```bash
podman-compose up -d --build
podman-compose ps
podman-compose logs --tail=100 db
podman-compose logs --tail=100 server
```

首次启动数据库会执行 `sql/init.sql`。如果 `db-data` 卷已经存在，MariaDB 不会重复导入初始化 SQL。

## 5. 验收

```bash
curl http://127.0.0.1/api/products
curl http://47.109.93.162/api/products
```

浏览器访问：

- `http://47.109.93.162/`
- `http://47.109.93.162/admin/`

后台默认账号来自 `sql/init.sql`。首次登录后立即修改默认密码。

## 6. 常用命令

```bash
podman-compose ps
podman-compose logs -f server
podman-compose logs -f nginx
podman-compose restart server
podman-compose down
podman-compose up -d --build
```

## 7. 数据备份与恢复

备份：

```bash
podman exec xiyuguopu-db sh -c 'mariadb-dump -uroot -p"$MARIADB_ROOT_PASSWORD" xiyuguopu' > /root/xiyuguopu_backup.sql
```

恢复到空库：

```bash
cat /root/xiyuguopu_backup.sql | podman exec -i xiyuguopu-db sh -c 'mariadb -uroot -p"$MARIADB_ROOT_PASSWORD" xiyuguopu'
```

清空全部容器和数据库卷：

```bash
podman-compose down
podman volume ls | grep db-data
podman volume rm 项目前缀_db-data
```
