# 西域果铺公网 IP 测试部署

本指南用于阿里云 Linux 3.2104 LTS，先用公网 IP `47.109.93.162` 跑通官网、后台和后端 API。

## 1. 安装基础环境

```bash
sudo dnf makecache
sudo dnf install -y java-17-openjdk nginx mariadb-server
sudo systemctl enable --now mariadb nginx
```

阿里云安全组先放行 `22` 和 `80`，不要开放 `3306`。

## 2. 初始化数据库

```bash
sudo mysql
```

```sql
CREATE DATABASE xiyuguopu DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'xiyuguopu'@'localhost' IDENTIFIED BY '换成强密码';
GRANT ALL PRIVILEGES ON xiyuguopu.* TO 'xiyuguopu'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

上传项目 SQL 后导入：

```bash
mysql -u xiyuguopu -p xiyuguopu < /opt/xiyuguopu/sql/init.sql
```

后台第一次登录后立刻修改默认管理员密码。

## 3. 准备目录和文件

```bash
sudo mkdir -p /opt/xiyuguopu/server /opt/xiyuguopu/web /opt/xiyuguopu/sql
sudo chown -R $USER:$USER /opt/xiyuguopu
```

需要放到服务器：

- 后端 jar：`/opt/xiyuguopu/server/xiyuguopu-server.jar`
- 官网和后台静态文件：`/opt/xiyuguopu/web/`
- 图片：`/opt/xiyuguopu/web/images/`
- SQL：`/opt/xiyuguopu/sql/init.sql`

## 4. 创建后端服务

生成一个至少 32 位的 JWT 密钥：

```bash
openssl rand -base64 48
```

创建服务文件：

```bash
sudo vi /etc/systemd/system/xiyuguopu.service
```

```ini
[Unit]
Description=XiyuGuopu Spring Boot Server
After=network.target mariadb.service

[Service]
WorkingDirectory=/opt/xiyuguopu/server
ExecStart=/usr/bin/java -jar /opt/xiyuguopu/server/xiyuguopu-server.jar
Environment=SPRING_PROFILES_ACTIVE=prod
Environment=MYSQL_PASSWORD=换成数据库密码
Environment=XIYU_JWT_SECRET=换成至少32字符随机密钥
Environment=APP_CORS_ALLOWED_ORIGINS=http://47.109.93.162
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
```

启动：

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now xiyuguopu
sudo systemctl status xiyuguopu
```

## 5. 配置 Nginx

```bash
sudo vi /etc/nginx/conf.d/xiyuguopu.conf
```

```nginx
server {
    listen 80;
    server_name 47.109.93.162;

    root /opt/xiyuguopu/web;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /admin/ {
        try_files $uri $uri/ /admin/index.html;
    }

    location /images/ {
        alias /opt/xiyuguopu/web/images/;
    }

    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

```bash
sudo nginx -t
sudo systemctl reload nginx
```

## 6. 验收

```bash
curl http://127.0.0.1:8080/api/products
curl http://47.109.93.162/api/products
```

浏览器打开：

- `http://47.109.93.162/`
- `http://47.109.93.162/admin/`

小程序开发调试可使用 `miniprogram/utils/api.js` 的 `prod` 地址。公网 IP 测试阶段需要在微信开发者工具里勾选“不校验合法域名”。正式体验版/上线仍需要域名和 HTTPS。
