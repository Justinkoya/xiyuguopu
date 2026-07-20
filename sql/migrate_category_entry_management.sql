USE xiyuguopu;

ALTER TABLE category
  ADD COLUMN entry_type VARCHAR(16) NOT NULL DEFAULT 'PRODUCT' COMMENT '入口类型(PRODUCT/PACKAGE)' AFTER code,
  ADD COLUMN is_enabled TINYINT(1) DEFAULT 1 COMMENT '是否展示' AFTER sort_order;

UPDATE category SET entry_type = 'PRODUCT' WHERE entry_type IS NULL OR entry_type = '';
UPDATE category SET entry_type = 'PACKAGE' WHERE code = 'gift';
UPDATE category SET is_enabled = 1 WHERE is_enabled IS NULL;
