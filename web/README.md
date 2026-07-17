# 西域果铺官网

纯静态展示型官网，通过微信云开发静态网站托管部署，和小程序共用一个平台。

## 项目结构

```
web/
├── index.html          # 主页面
├── css/
│   └── style.css       # 自定义样式（动画、卡片效果等）
├── js/
│   ├── products.js     # ⭐ 所有可配置数据（改价格改这里）
│   └── main.js         # 渲染逻辑 + 页面交互
├── images/             # 产品图片（28 张）
└── cloudbaserc.json    # 云开发部署配置
```

## 如何改价格 / 改产品

**只需改一个文件：`js/products.js`**

保存后刷新浏览器即生效，页面自动更新。

### 改产品价格

```js
// js/products.js

PRODUCTS.trial:      // 尝鲜精选（3 款）
PRODUCTS.premium:    // 西域珍品（6 款）
PRODUCTS.bestseller: // 口碑好物（4 款）
```

每款产品的数据结构：

```js
{
  name: '纸皮核桃',      // 产品名
  price: 19.9,           // 价格
  unit: '500g',          // 单位
  image: 'images/walnut.png',
  badge: '初次尝鲜',      // 角标文字
  tags: ['阿克苏185品种', '手捏即开'],
  desc: '壳薄如纸，果仁饱满不空壳'
}
```

### 改套餐价格

```js
PACKAGES 数组 → 修改 price 字段即可
```

### 改成本透明明细

```js
COST_BREAKDOWN 数组 → 每项成本独立可调
```

### 改评分卡

```js
SCORECARDS 数组 → 每款产品 6 个维度的分数
```

### 改站点信息

```js
SITE = {
  brandName: '西域果铺',
  wechat: 'xiyuguopu',
  year: 2025
}
```

## 如何部署

### 微信云开发静态网站托管（推荐）

和小程序共用一个云开发环境，国内访问快，有免费额度。

#### 方式一：微信开发者工具（最简单）

1. 打开微信开发者工具 → 云开发控制台
2. 左侧菜单 → **静态网站托管**
3. 开通后，点击**上传文件**
4. 把 `web/` 目录下所有文件拖进去
5. 获得默认域名 `xxx.tcloudbase.com`，可直接访问

#### 方式二：CloudBase CLI（适合自动化）

```bash
# 安装 CLI
npm install -g @cloudbase/cli

# 登录
tcb login

# 修改 cloudbaserc.json 中的 envId 为你的环境 ID
# 然后在 web 目录执行：
tcb hosting deploy . -e 你的环境ID
```

> 正式上线后可以绑定自己的域名（需备案）。

### 本地预览

用任意静态服务器即可：

```bash
# 方式一：Python
python -m http.server 8080

# 方式二：Node.js
npx serve .

# 方式三：VS Code Live Server 插件
```

然后浏览器打开 `http://localhost:8080`

## 技术栈

- Tailwind CSS（CDN）
- Font Awesome 图标（CDN）
- Google Fonts（中文免费字体）
- 纯原生 JS，零依赖
