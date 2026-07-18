USE xiyuguopu;

INSERT INTO category (name, code, theme_color, sort_order)
SELECT '精选礼品', 'gift', 'red', 4
WHERE NOT EXISTS (
  SELECT 1 FROM category WHERE code = 'gift'
);
