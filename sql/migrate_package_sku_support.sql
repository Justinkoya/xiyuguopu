USE xiyuguopu;

ALTER TABLE package_def
  ADD COLUMN stock INT DEFAULT 999 COMMENT '库存' AFTER price,
  ADD COLUMN sale INT DEFAULT 0 COMMENT '销量' AFTER stock;

UPDATE package_def SET stock = 999, sale = 88 WHERE code = 'trial';
UPDATE package_def SET stock = 999, sale = 156 WHERE code = 'gift';
UPDATE package_def SET stock = 999, sale = 73 WHERE code = 'family';

UPDATE package_product SET product_name = '喀什西梅干' WHERE product_name = '西梅干';
UPDATE package_product SET product_name = '大无花果干' WHERE product_name = '无花果干';

-- 如果 order_item.product_id 已有外键约束，需要先查询约束名并删除：
-- SELECT CONSTRAINT_NAME
-- FROM information_schema.KEY_COLUMN_USAGE
-- WHERE TABLE_SCHEMA = DATABASE()
--   AND TABLE_NAME = 'order_item'
--   AND COLUMN_NAME = 'product_id'
--   AND REFERENCED_TABLE_NAME = 'product';
-- ALTER TABLE order_item DROP FOREIGN KEY 查询到的约束名;

ALTER TABLE order_item
  ADD COLUMN item_type VARCHAR(16) NOT NULL DEFAULT 'PRODUCT' COMMENT 'PRODUCT/PACKAGE' AFTER order_id,
  MODIFY COLUMN product_id BIGINT NULL COMMENT '商品ID',
  ADD COLUMN package_code VARCHAR(32) DEFAULT '' COMMENT '套餐编码' AFTER product_id;
