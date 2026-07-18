USE xiyuguopu;

ALTER TABLE package_def
  ADD COLUMN image VARCHAR(255) DEFAULT '' COMMENT '图片路径' AFTER icon;

UPDATE package_def SET image = 'images/assortment.png' WHERE code = 'trial';
UPDATE package_def SET image = 'images/gift-scene.png' WHERE code = 'gift';
UPDATE package_def SET image = 'images/assortment.png' WHERE code = 'family';
