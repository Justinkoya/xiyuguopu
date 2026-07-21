-- ================================================================
-- 西域果铺 - 常用查询索引优化
-- MySQL / MariaDB
--
-- 用途：
-- 1. 删除 order_head.order_no 上的重复普通索引 idx_order_no，保留 UNIQUE。
-- 2. 补充商品列表、首页推荐、订单后台常用查询索引。
--
-- 执行前建议先备份数据库：
-- podman exec xiyuguopu-db sh -c 'mariadb-dump -uroot -p"$MARIADB_ROOT_PASSWORD" xiyuguopu' > /root/backup_before_indexes.sql
-- ================================================================

USE xiyuguopu;

DELIMITER //

DROP PROCEDURE IF EXISTS add_index_if_missing//
CREATE PROCEDURE add_index_if_missing(
  IN p_table_name VARCHAR(64),
  IN p_index_name VARCHAR(64),
  IN p_index_sql TEXT
)
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = p_table_name
      AND index_name = p_index_name
  ) THEN
    SET @sql = p_index_sql;
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END//

DROP PROCEDURE IF EXISTS drop_index_if_exists//
CREATE PROCEDURE drop_index_if_exists(
  IN p_table_name VARCHAR(64),
  IN p_index_name VARCHAR(64)
)
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = p_table_name
      AND index_name = p_index_name
  ) THEN
    SET @sql = CONCAT('ALTER TABLE `', p_table_name, '` DROP INDEX `', p_index_name, '`');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END//

DELIMITER ;

-- order_no 已有 UNIQUE 约束，idx_order_no 是重复普通索引。
CALL drop_index_if_exists('order_head', 'idx_order_no');

-- 商品列表：按分类、上下架、排序查。
CALL add_index_if_missing(
  'product',
  'idx_product_category_sale_sort',
  'ALTER TABLE product ADD INDEX idx_product_category_sale_sort (category_id, is_on_sale, sort_order)'
);

-- 首页/推荐商品：按推荐、上下架、排序查。
CALL add_index_if_missing(
  'product',
  'idx_product_featured_sale_sort',
  'ALTER TABLE product ADD INDEX idx_product_featured_sale_sort (featured, is_on_sale, sort_order)'
);

-- 后台商品搜索：支持按商品名 LIKE 查询的基础索引。
CALL add_index_if_missing(
  'product',
  'idx_product_name',
  'ALTER TABLE product ADD INDEX idx_product_name (name)'
);

-- 后台订单列表：按状态筛选并按创建时间排序。
CALL add_index_if_missing(
  'order_head',
  'idx_order_status_created',
  'ALTER TABLE order_head ADD INDEX idx_order_status_created (status, created_at)'
);

-- 用户订单列表：按用户查看订单并按创建时间排序。
CALL add_index_if_missing(
  'order_head',
  'idx_order_user_created',
  'ALTER TABLE order_head ADD INDEX idx_order_user_created (user_id, created_at)'
);

DROP PROCEDURE IF EXISTS add_index_if_missing;
DROP PROCEDURE IF EXISTS drop_index_if_exists;
