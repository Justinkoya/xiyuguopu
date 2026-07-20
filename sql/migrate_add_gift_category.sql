USE xiyuguopu;

-- 先执行 migrate_category_entry_management.sql 增加 entry_type / is_enabled 字段。
INSERT INTO category (name, code, entry_type, theme_color, sort_order, is_enabled)
SELECT '精选礼品', 'gift', 'PACKAGE', 'red', 4, 1
WHERE NOT EXISTS (
  SELECT 1 FROM category WHERE code = 'gift'
);

UPDATE category SET entry_type = 'PACKAGE' WHERE code = 'gift';
