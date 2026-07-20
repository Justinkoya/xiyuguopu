USE xiyuguopu;

ALTER TABLE product
  ADD COLUMN featured TINYINT(1) DEFAULT 0 COMMENT '是否首页推荐' AFTER description;

UPDATE product
SET featured = 1
WHERE name IN ('纸皮核桃', '喀什西梅干', '大无花果干', '奶枣巴旦木', '开心果');
