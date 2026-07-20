# 西域果铺 Docker 并行测试部署

本指南用于在阿里云 Linux 3 上先用 `8088` 端口运行 Docker 版，不影响当前宿主机 Nginx 的 `80` 端口。

## 1. 安装 Docker

```bash
dnf install -y docker docker-compose-plugin
systemctl enable --now docker
docker version
docker compose version
```

阿里云安全组临时放行 TCP `8088`。

## 2. 拉取代码

```bash
cd /opt
git clone https://github.com/Justinkoya/xiyuguopu.git xiyuguopu-docker
cd /opt/xiyuguopu-docker
git checkout codex/server-ip-deploy
```

如果服务器已经 clone 过：

```bash
cd /opt/xiyuguopu-docker
git fetch origin
git checkout codex/server-ip-deploy
git pull
```

## 3. 创建环境变量

```bash
cp .env.example .env
vi .env
```

至少修改：

```dotenv
MYSQL_PASSWORD=当前宿主 MariaDB 的 xiyuguopu 用户密码
XIYU_JWT_SECRET=至少32字符随机密钥
```

保留：

```dotenv
MYSQL_HOST=host.docker.internal
NGINX_PORT=8088
APP_CORS_ALLOWED_ORIGINS=http://47.109.93.162,http://47.109.93.162:8088
```

## 4. 允许 Docker 访问宿主 MariaDB

容器会通过 `host.docker.internal` 访问宿主机 MariaDB。宿主 MariaDB 需要监听 Docker 网桥，并允许 Docker 网段用户登录。

编辑 MariaDB 配置：

```bash
vi /etc/my.cnf.d/mariadb-server.cnf
```

在 `[mysqld]` 下确认或新增：

```ini
bind-address=0.0.0.0
```

重启 MariaDB：

```bash
systemctl restart mariadb
```

授权 Docker 网桥访问。这里的密码要和 `.env` 里的 `MYSQL_PASSWORD` 一致：

```bash
mysql -u root
```

```sql
CREATE USER IF NOT EXISTS 'xiyuguopu'@'172.17.%' IDENTIFIED BY '当前数据库密码';
GRANT ALL PRIVILEGES ON xiyuguopu.* TO 'xiyuguopu'@'172.17.%';
FLUSH PRIVILEGES;
EXIT;
```

确认宿主没有把 `3306` 暴露到公网安全组；阿里云安全组仍然不要放行 `3306`。

## 5. 启动并行测试环境

```bash
docker compose up -d --build
docker compose ps
docker compose logs --tail=100 server
```

验证：

```bash
curl http://127.0.0.1:8088/api/products
curl http://47.109.93.162:8088/api/products
```

浏览器访问：

- `http://47.109.93.162:8088/`
- `http://47.109.93.162:8088/admin/`

## 6. 常用命令

```bash
docker compose logs -f server
docker compose logs -f nginx
docker compose restart server
docker compose down
docker compose up -d --build
```

## 7. 切换到正式 80 端口

确认 Docker 版 `8088` 没问题后，再执行：

```bash
systemctl stop xiyuguopu
systemctl stop nginx
```

编辑 `.env`：

```dotenv
NGINX_PORT=80
APP_CORS_ALLOWED_ORIGINS=http://47.109.93.162
```

重启 Docker：

```bash
docker compose up -d
curl http://47.109.93.162/api/products
```
