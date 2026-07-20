USE xiyuguopu;

-- Category 管理测试脚本
-- 说明：
-- 1. 该脚本使用事务，最后 ROLLBACK，不会保留测试数据。
-- 2. 如果你的 MySQL 客户端开了 autocommit，也可以正常执行；DDL 不在本脚本内。
-- 3. 主要验证字段、启用过滤、排序、PRODUCT 分类删除保护、PACKAGE 入口可删除。

START TRANSACTION;

SELECT '1. category 表字段' AS step;
SHOW COLUMNS FROM category WHERE Field IN ('entry_type', 'is_enabled');

SELECT '2. 当前小程序应展示的分类：只看 is_enabled=1，按 sort_order/id 排序' AS step;
SELECT id, name, code, entry_type, theme_color, sort_order, is_enabled
FROM category
WHERE is_enabled = 1
ORDER BY sort_order ASC, id ASC;

SELECT '2.1 theme_color 预设值' AS step;
SELECT 'blue, amber, green, red, purple, pink, orange, teal, cyan, lime, brown, slate' AS supported_theme_colors;

SELECT '3. 新增一个 PRODUCT 测试分类和一个 PACKAGE 测试入口' AS step;
INSERT INTO category (name, code, entry_type, theme_color, sort_order, is_enabled)
VALUES
  ('测试商品分类', 'test_product_category', 'PRODUCT', 'blue', 990, 1),
  ('测试套餐入口', 'test_package_category', 'PACKAGE', 'red', 991, 1);

SET @test_product_category_id := (
  SELECT id FROM category WHERE code = 'test_product_category' LIMIT 1
);

SET @test_package_category_id := (
  SELECT id FROM category WHERE code = 'test_package_category' LIMIT 1
);

SELECT id, name, code, entry_type, theme_color, sort_order, is_enabled
FROM category
WHERE code IN ('test_product_category', 'test_package_category')
ORDER BY sort_order ASC;

SELECT '4. 给 PRODUCT 测试分类挂一个测试商品，用来验证删除保护' AS step;
INSERT INTO product (
  category_id, name, price, unit, image, badge, tags, description,
  featured, highlight, highlight_text, extra, stock, sale, is_on_sale, sort_order
) VALUES (
  @test_product_category_id, '测试分类删除保护商品', 1.00, '份', '', '', JSON_ARRAY(),
  '用于验证分类下有商品时不能删除', 0, 0, '', '', 1, 0, 1, 999
);

SELECT c.id, c.name, c.entry_type, COUNT(p.id) AS product_count
FROM category c
LEFT JOIN product p ON p.category_id = c.id
WHERE c.id IN (@test_product_category_id, @test_package_category_id)
GROUP BY c.id, c.name, c.entry_type
ORDER BY c.id;

SELECT '5. 模拟后端 PRODUCT 删除保护判断：下面 product_count > 0，所以后端应禁止删除' AS step;
SELECT COUNT(*) AS product_count_should_block_delete
FROM product
WHERE category_id = @test_product_category_id;

SELECT '6. 模拟 PACKAGE 入口删除：没有商品外键依赖，删除应允许' AS step;
DELETE FROM category WHERE id = @test_package_category_id;

SELECT COUNT(*) AS package_category_remaining_should_be_0
FROM category
WHERE code = 'test_package_category';

SELECT '7. 回滚测试数据' AS step;
ROLLBACK;

SELECT '8. 回滚后确认测试数据不存在' AS step;
SELECT code, COUNT(*) AS count_after_rollback
FROM category
WHERE code IN ('test_product_category', 'test_package_category')
GROUP BY code;
