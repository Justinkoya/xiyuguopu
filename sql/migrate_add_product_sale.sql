USE xiyuguopu;

ALTER TABLE product
  ADD COLUMN sale INT DEFAULT 0 COMMENT '销量' AFTER stock;

UPDATE product SET sale = 128 WHERE name = '纸皮核桃';
UPDATE product SET sale = 96 WHERE name = '情人梅';
UPDATE product SET sale = 158 WHERE name = '喀什西梅干';
UPDATE product SET sale = 82 WHERE name = '大无花果干';
UPDATE product SET sale = 45 WHERE name = '沙漠果';
UPDATE product SET sale = 76 WHERE name = '玫瑰切糕';
UPDATE product SET sale = 64 WHERE name = '椰枣';
UPDATE product SET sale = 112 WHERE name = '库车小白杏';
UPDATE product SET sale = 139 WHERE name = '奶枣巴旦木';
UPDATE product SET sale = 231 WHERE name = '开心果';
UPDATE product SET sale = 174 WHERE name = '酸奶巴旦木';
UPDATE product SET sale = 146 WHERE name = '小白杏';
UPDATE product SET sale = 119 WHERE name = '夏威夷果';
