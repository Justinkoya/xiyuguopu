-- ================================================================
-- 西域果铺 — 数据库初始化脚本
-- MySQL 8.0 / utf8mb4
-- ================================================================

CREATE DATABASE IF NOT EXISTS xiyuguopu
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE xiyuguopu;

-- ================================================================
-- 1. 商品分类
-- ================================================================
DROP TABLE IF EXISTS category;
CREATE TABLE category (
  id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(32)   NOT NULL COMMENT '分类名',
  code        VARCHAR(32)   NOT NULL UNIQUE COMMENT '分类编码(trial/premium/bestseller)',
  entry_type  VARCHAR(16)   NOT NULL DEFAULT 'PRODUCT' COMMENT '入口类型(PRODUCT/PACKAGE)',
  theme_color VARCHAR(16)   NOT NULL COMMENT '前端色系(blue/amber/green/red/purple/pink/orange/teal/cyan/lime/brown/slate)',
  sort_order  INT           DEFAULT 0,
  is_enabled  TINYINT(1)    DEFAULT 1 COMMENT '是否展示',
  created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='商品分类';

INSERT INTO category (name, code, entry_type, theme_color, sort_order, is_enabled) VALUES
('尝鲜精选', 'trial',      'PRODUCT', 'blue',  1, 1),
('西域珍品', 'premium',    'PRODUCT', 'amber', 2, 1),
('口碑好物', 'bestseller', 'PRODUCT', 'green', 3, 1),
('精选礼品', 'gift',       'PACKAGE', 'red',   4, 1);

-- ================================================================
-- 2. 商品
-- ================================================================
DROP TABLE IF EXISTS product;
CREATE TABLE product (
  id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
  category_id BIGINT        NOT NULL COMMENT '分类ID',
  name        VARCHAR(64)   NOT NULL COMMENT '商品名',
  price       DECIMAL(8,2)  NOT NULL COMMENT '售价',
  unit        VARCHAR(16)   DEFAULT '500g' COMMENT '单位',
  image       VARCHAR(255)  DEFAULT '' COMMENT '图片路径',
  badge       VARCHAR(32)   DEFAULT '' COMMENT '角标文字',
  tags        JSON          COMMENT '标签(JSON数组)',
  description VARCHAR(255)  DEFAULT '' COMMENT '一句话描述',
  featured    TINYINT(1)    DEFAULT 0 COMMENT '是否首页推荐',
  highlight   TINYINT(1)    DEFAULT 0 COMMENT '是否高亮展示',
  highlight_text VARCHAR(32) DEFAULT '' COMMENT '高亮角标文字',
  extra       VARCHAR(255)  DEFAULT '' COMMENT '额外信息',
  stock       INT           DEFAULT 0 COMMENT '库存',
  sale        INT           DEFAULT 0 COMMENT '销量',
  is_on_sale  TINYINT(1)    DEFAULT 1 COMMENT '是否上架',
  sort_order  INT           DEFAULT 0 COMMENT '排序',
  cost_detail JSON          COMMENT '成本明细(JSON → 前端透明展示)',
  created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB COMMENT='商品';

-- ===== 尝鲜精选（trial, category_id=1）=====
INSERT INTO product (category_id, name, price, unit, image, badge, tags, description, featured, highlight, highlight_text, extra, stock, sale, sort_order) VALUES
(1, '纸皮核桃',  29.9, '500g', 'images/walnut.png',      '初次尝鲜', '["阿克苏185品种","手捏即开"]',                 '壳薄如纸，果仁饱满不空壳',           1, 0, '', '', 999, 128, 1),
(1, '情人梅',    19.9, '500g', 'images/lovers-plum.png',  '零嘴必备', '["酸甜解腻","无糖精"]',                         '果肉厚实，酸甜恰到好处',             0, 0, '', '', 999, 96, 2),
(1, '喀什西梅干', 25.9, '500g', 'images/prune.png',       '网红人气', '["自然晾晒","糯香酸甜"]',                       '女生最爱，办公室零食人气王',         1, 0, '', '', 999, 158, 3);

-- ===== 西域珍品（premium, category_id=2）=====
INSERT INTO product (category_id, name, price, unit, image, badge, tags, description, featured, highlight, highlight_text, extra, stock, sale, sort_order) VALUES
(2, '大无花果干', 49.9, '500g', 'images/fig.png',          '全网好价', '["小品种稀缺","低于1688价"]',                   '新疆小无花果，全网没几家有货',       1, 0, '', '', 999, 82, 1),
(2, '沙漠果',     49.9, '500g', 'images/brazil.png',       '新奇体验', '["99%人没吃过","社交话题"]',                    '名字自带新疆画面感的神秘坚果',       0, 0, '', '', 999, 45, 2),
(2, '玫瑰切糕',   45.9, '500g', 'images/rosecake.png',     '手工点心', '["纯手工制作","差异化极强"]',                    '玫瑰花+核桃仁，传统新疆手工点心',    0, 0, '', '', 999, 76, 3),
(2, '椰枣',       55.9, '500g', 'images/dates.png',        '异域珍品', '["新疆少数民族特产","市面少见"]',                '比巧克力还好吃的天然甜糯珍果',       0, 0, '', '', 999, 64, 4),
(2, '库车小白杏', 39.9, '500g', 'images/apricot.png',      '库车产地', '["一杏两吃","产地标签"]',                       '新疆独有品种，杏肉软糯杏仁酥脆',     0, 0, '', '', 999, 112, 5),
(2, '奶枣巴旦木', 45.9, '500g', 'images/date-almond.png',  '网红爆款', '["红枣+巴旦木","进价优势"]',                    '网红零食，年轻人疯狂回购',           1, 0, '', '', 999, 139, 6);

-- ===== 口碑好物（bestseller, category_id=3）=====
INSERT INTO product (category_id, name, price, unit, image, badge, tags, description, featured, highlight, highlight_text, extra, stock, sale, sort_order) VALUES
(3, '开心果',     59.9, '500g', 'images/pistachio.png',    '',         '[]',                                            '颗颗自然开口，不漂白',               1, 1, '回购首选', '进价低于1688批发价', 999, 231, 1),
(3, '酸奶巴旦木', 52.9, '500g', 'images/yogurt-almond.png','',         '[]',                                            '酸奶包裹，酸甜酥脆口感',             0, 0, '',         '',                   999, 174, 2),
(3, '小白杏',     39.9, '500g', 'images/apricot.png',      '',         '[]',                                            '配料只有杏肉，0添加',                0, 0, '',         '进价低于1688批发价', 999, 146, 3),
(3, '夏威夷果',   49.9, '500g', 'images/macadamia.png',    '',         '[]',                                            '奶香浓郁，圆润饱满',                 0, 0, '',         '',                   999, 119, 4);

-- ================================================================
-- 3. 成本透明明细
-- ================================================================
DROP TABLE IF EXISTS cost_breakdown;
CREATE TABLE cost_breakdown (
  id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
  product_id  BIGINT        NOT NULL COMMENT '商品ID',
  icon        VARCHAR(8)    DEFAULT '' COMMENT '展示图标',
  price       DECIMAL(8,2)  NOT NULL COMMENT '售价',
  weight      VARCHAR(16)   DEFAULT '500g',
  profit      DECIMAL(8,2)  NOT NULL COMMENT '利润',
  profit_pct  VARCHAR(8)    DEFAULT '' COMMENT '利润率',
  created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB COMMENT='成本透明明细(主表)';

DROP TABLE IF EXISTS cost_breakdown_item;
CREATE TABLE cost_breakdown_item (
  id                BIGINT        AUTO_INCREMENT PRIMARY KEY,
  breakdown_id      BIGINT        NOT NULL COMMENT '成本明细ID',
  label             VARCHAR(64)   NOT NULL COMMENT '成本项名称',
  amount            DECIMAL(8,2)  NOT NULL COMMENT '金额',
  pct               VARCHAR(8)    DEFAULT '' COMMENT '占比',
  sort_order        INT           DEFAULT 0,
  FOREIGN KEY (breakdown_id) REFERENCES cost_breakdown(id)
) ENGINE=InnoDB COMMENT='成本透明明细(子项)';

-- 开心果成本
INSERT INTO cost_breakdown (id, product_id, icon, price, weight, profit, profit_pct) VALUES
(1, (SELECT id FROM product WHERE name='开心果'), '果', 59.9, '500g', 12.9, '21.5%');
INSERT INTO cost_breakdown_item (breakdown_id, label, amount, pct, sort_order) VALUES
(1, '喀什产地收购价',      35.0, '58.4%', 1),
(1, '新疆→内地冷链物流',    5.0, '8.3%',  2),
(1, '独立包装+人工分拣',    4.0, '6.7%',  3),
(1, '快递包邮费',           3.0, '5.0%',  4);

-- 小白杏成本
INSERT INTO cost_breakdown (id, product_id, icon, price, weight, profit, profit_pct) VALUES
(2, (SELECT id FROM product WHERE name='小白杏' AND category_id=3), '杏', 39.9, '500g', 10.4, '26.1%');
INSERT INTO cost_breakdown_item (breakdown_id, label, amount, pct, sort_order) VALUES
(2, '库车产地收购价',      17.5, '43.9%', 1),
(2, '新疆→内地物流',       5.0, '12.5%', 2),
(2, '独立包装+人工分拣',    4.0, '10.0%', 3),
(2, '快递包邮费',           3.0, '7.5%',  4);

-- ================================================================
-- 4. 6星评分卡
-- ================================================================
DROP TABLE IF EXISTS scorecard;
CREATE TABLE scorecard (
  id              BIGINT        AUTO_INCREMENT PRIMARY KEY,
  product_id      BIGINT        NOT NULL COMMENT '商品ID',
  total_score     DECIMAL(3,1)  NOT NULL COMMENT '综合评分(/6)',
  origin_text     VARCHAR(128)  DEFAULT '' COMMENT '产地描述',
  image           VARCHAR(255)  DEFAULT '' COMMENT '图片',
  ingredient_text VARCHAR(255)  DEFAULT '' COMMENT '配料说明',
  created_at      DATETIME      DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB COMMENT='6星评分卡(主表)';

DROP TABLE IF EXISTS scorecard_dim;
CREATE TABLE scorecard_dim (
  id            BIGINT        AUTO_INCREMENT PRIMARY KEY,
  scorecard_id  BIGINT        NOT NULL COMMENT '评分卡ID',
  label         VARCHAR(16)   NOT NULL COMMENT '维度名(产地透明/颗粒均匀/...)',
  score         DECIMAL(3,1)  NOT NULL COMMENT '分数(/6)',
  pct           INT           DEFAULT 0 COMMENT '百分比(0-100)',
  sort_order    INT           DEFAULT 0,
  FOREIGN KEY (scorecard_id) REFERENCES scorecard(id)
) ENGINE=InnoDB COMMENT='6星评分卡(维度)';

-- 开心果
INSERT INTO scorecard (id, product_id, total_score, origin_text, image, ingredient_text) VALUES
(1, (SELECT id FROM product WHERE name='开心果'), 5.7, '喀什疏附县 · 自然开口', 'images/pistachio.png', '配料：开心果，食用盐');
INSERT INTO scorecard_dim (scorecard_id, label, score, pct, sort_order) VALUES
(1, '产地透明', 6.0, 100, 1),
(1, '颗粒均匀', 5.5, 92,  2),
(1, '干湿适口', 5.8, 96,  3),
(1, '洁净免洗', 6.0, 100, 4),
(1, '口感稳定', 5.4, 90,  5),
(1, '配料纯净', 6.0, 100, 6);

-- 小白杏干
INSERT INTO scorecard (id, product_id, total_score, origin_text, image, ingredient_text) VALUES
(2, (SELECT id FROM product WHERE name='小白杏' AND category_id=3), 5.8, '库车 · 自然晾晒', 'images/apricot.png', '配料：杏肉（只有这一个）');
INSERT INTO scorecard_dim (scorecard_id, label, score, pct, sort_order) VALUES
(2, '产地透明', 6.0, 100, 1),
(2, '颗粒均匀', 5.5, 92,  2),
(2, '干湿适口', 5.8, 96,  3),
(2, '洁净免洗', 6.0, 100, 4),
(2, '口感稳定', 6.0, 100, 5),
(2, '配料纯净', 6.0, 100, 6);

-- 无花果干
INSERT INTO scorecard (id, product_id, total_score, origin_text, image, ingredient_text) VALUES
(3, (SELECT id FROM product WHERE name='大无花果干'), 5.5, '喀什 · 传统晒制', 'images/fig.png', '配料：无花果（只有这一个）');
INSERT INTO scorecard_dim (scorecard_id, label, score, pct, sort_order) VALUES
(3, '产地透明', 6.0, 100, 1),
(3, '颗粒均匀', 5.2, 87,  2),
(3, '干湿适口', 5.5, 92,  3),
(3, '洁净免洗', 6.0, 100, 4),
(3, '口感稳定', 5.3, 88,  5),
(3, '配料纯净', 6.0, 100, 6);

-- ================================================================
-- 5. 套餐定义
-- ================================================================
DROP TABLE IF EXISTS package_def;
CREATE TABLE package_def (
  id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
  code        VARCHAR(16)   NOT NULL UNIQUE COMMENT '套餐编码(trial/gift/family)',
  icon        VARCHAR(8)    DEFAULT '' COMMENT '图标文字',
  image       VARCHAR(255)  DEFAULT '' COMMENT '图片路径',
  name        VARCHAR(32)   NOT NULL COMMENT '套餐名',
  subtitle    VARCHAR(128)  DEFAULT '' COMMENT '副标题',
  price       DECIMAL(8,2)  NOT NULL COMMENT '套餐价',
  stock       INT           DEFAULT 999 COMMENT '库存',
  sale        INT           DEFAULT 0 COMMENT '销量',
  featured    TINYINT(1)    DEFAULT 0 COMMENT '是否推荐(高亮展示)',
  badge       VARCHAR(32)   DEFAULT '' COMMENT '推荐角标文字',
  extra       VARCHAR(255)  DEFAULT '' COMMENT '额外信息',
  sort_order  INT           DEFAULT 0,
  created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='套餐定义';

INSERT INTO package_def (code, icon, image, name, subtitle, price, stock, sale, featured, badge, extra, sort_order) VALUES
('trial',  '新', 'images/assortment.png', '尝鲜包',     '3款经典搭配 · 首次体验', 92.7,  999, 88,  0, '',       '',              1),
('gift',   '礼', 'images/gift-scene.png', '尊享礼盒',   '6款精选 · 送礼自用',     281.4, 999, 156, 1, '最受欢迎', '',           2),
('family', '家', 'images/assortment.png', '家庭囤货装', '8款全包 · 够吃一个月',   327.2, 999, 73,  0, '',       '月均仅需327元', 3);

DROP TABLE IF EXISTS package_product;
CREATE TABLE package_product (
  id            BIGINT  AUTO_INCREMENT PRIMARY KEY,
  package_id    BIGINT  NOT NULL COMMENT '套餐ID',
  product_name  VARCHAR(64) NOT NULL COMMENT '商品名(冗余)',
  quantity      VARCHAR(32) DEFAULT '500g' COMMENT '规格数量',
  sort_order    INT     DEFAULT 0,
  FOREIGN KEY (package_id) REFERENCES package_def(id)
) ENGINE=InnoDB COMMENT='套餐包含商品';

-- 尝鲜包
INSERT INTO package_product (package_id, product_name, quantity, sort_order) VALUES
(1, '纸皮核桃',   '500g', 1),
(1, '小白杏',     '500g', 2),
(1, '喀什西梅干', '500g', 3);

-- 尊享礼盒
INSERT INTO package_product (package_id, product_name, quantity, sort_order) VALUES
(2, '开心果',     '500g', 1),
(2, '大无花果干', '500g', 2),
(2, '酸奶巴旦木', '500g', 3),
(2, '小白杏',     '500g', 4),
(2, '喀什西梅干', '500g', 5),
(2, '玫瑰切糕',   '500g', 6);

-- 家庭囤货装（模糊描述）
INSERT INTO package_product (package_id, product_name, quantity, sort_order) VALUES
(3, '尝鲜精选2款', '500g', 1),
(3, '口碑好物4款', '500g', 2),
(3, '凑单惊喜2款', '500g', 3);

-- ================================================================
-- 6. 用户（小程序端）
-- ================================================================
DROP TABLE IF EXISTS user;
CREATE TABLE user (
  id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
  openid      VARCHAR(64)   NOT NULL UNIQUE COMMENT '微信openid',
  nickname    VARCHAR(64)   DEFAULT '' COMMENT '微信昵称',
  avatar      VARCHAR(512)  DEFAULT '' COMMENT '头像URL',
  phone       VARCHAR(20)   DEFAULT '' COMMENT '手机号',
  created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='用户';

-- ================================================================
-- 7. 收货地址
-- ================================================================
DROP TABLE IF EXISTS address;
CREATE TABLE address (
  id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT        NOT NULL COMMENT '用户ID',
  name        VARCHAR(32)   NOT NULL COMMENT '收货人',
  phone       VARCHAR(20)   NOT NULL COMMENT '联系电话',
  province    VARCHAR(16)   NOT NULL COMMENT '省',
  city        VARCHAR(16)   NOT NULL COMMENT '市',
  district    VARCHAR(16)   NOT NULL COMMENT '区',
  detail      VARCHAR(255)  NOT NULL COMMENT '详细地址',
  is_default  TINYINT(1)    DEFAULT 0 COMMENT '是否默认地址',
  created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB COMMENT='收货地址';

-- ================================================================
-- 8. 订单主表
-- ================================================================
DROP TABLE IF EXISTS order_head;
CREATE TABLE order_head (
  id                  BIGINT        AUTO_INCREMENT PRIMARY KEY,
  order_no            VARCHAR(32)   NOT NULL UNIQUE COMMENT '订单号',
  user_id             BIGINT        NOT NULL COMMENT '用户ID',
  address_id          BIGINT        COMMENT '收货地址ID',
  address_snapshot    JSON          COMMENT '地址快照(下单时完整保存)',
  total_amount        DECIMAL(8,2)  NOT NULL COMMENT '实付金额',
  status              VARCHAR(16)   NOT NULL DEFAULT 'UNPAID' COMMENT 'UNPAID/PAID/SHIPPED/COMPLETED/CANCELLED',
  wx_transaction_id   VARCHAR(64)   DEFAULT '' COMMENT '微信支付流水号',
  remark              VARCHAR(255)  DEFAULT '' COMMENT '用户备注',
  tracking_number     VARCHAR(64)   DEFAULT '' COMMENT '快递单号',
  shipping_company    VARCHAR(32)   DEFAULT '' COMMENT '快递公司',
  paid_at             DATETIME      COMMENT '支付时间',
  shipped_at          DATETIME      COMMENT '发货时间',
  completed_at        DATETIME      COMMENT '完成时间',
  cancelled_at        DATETIME      COMMENT '取消时间',
  created_at          DATETIME      DEFAULT CURRENT_TIMESTAMP,
  updated_at          DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id)  REFERENCES user(id),
  FOREIGN KEY (address_id) REFERENCES address(id),
  INDEX idx_order_no (order_no),
  INDEX idx_user_id (user_id),
  INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='订单主表';

-- ================================================================
-- 9. 订单明细
-- ================================================================
DROP TABLE IF EXISTS order_item;
CREATE TABLE order_item (
  id            BIGINT        AUTO_INCREMENT PRIMARY KEY,
  order_id      BIGINT        NOT NULL COMMENT '订单ID',
  item_type     VARCHAR(16)   NOT NULL DEFAULT 'PRODUCT' COMMENT 'PRODUCT/PACKAGE',
  product_id    BIGINT        COMMENT '商品ID',
  package_code  VARCHAR(32)   DEFAULT '' COMMENT '套餐编码',
  product_name  VARCHAR(64)   NOT NULL COMMENT '商品名(冗余快照)',
  price         DECIMAL(8,2)  NOT NULL COMMENT '下单时单价',
  quantity      INT           NOT NULL DEFAULT 1 COMMENT '数量',
  subtotal      DECIMAL(8,2)  NOT NULL COMMENT '小计',
  FOREIGN KEY (order_id)   REFERENCES order_head(id),
  INDEX idx_order_id (order_id)
) ENGINE=InnoDB COMMENT='订单明细';

-- ================================================================
-- 10. 后台管理员
-- ================================================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
  username    VARCHAR(32)   NOT NULL UNIQUE COMMENT '用户名',
  password    VARCHAR(255)  NOT NULL COMMENT '密码(BCrypt)',
  role        VARCHAR(16)   NOT NULL DEFAULT 'ADMIN' COMMENT 'ADMIN/OPERATOR',
  is_enabled  TINYINT(1)    DEFAULT 1,
  created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='后台管理员';

-- 默认管理员: admin / admin123  (BCrypt 加密，上线后务必修改)
INSERT INTO sys_user (username, password, role) VALUES
('admin', '$2a$10$tKhIX.YldQCWawRGP//ZreYeKdW1Mspx80nAh31B3FiZ3XmqgbaLe', 'ADMIN');

-- ================================================================
-- 完成
-- ================================================================
-- 默认管理员: admin / admin123  ← 上线前一定改掉！
-- 所有产品初始库存 = 999，正式运营前请据实修改
-- ================================================================
