-- ================================================================
-- 迁移：order_head 表加物流字段
-- 执行日期：2026-07-21
-- ================================================================

ALTER TABLE order_head
    ADD COLUMN tracking_number VARCHAR(64) DEFAULT '' COMMENT '快递单号' AFTER remark,
    ADD COLUMN shipping_company VARCHAR(32) DEFAULT '' COMMENT '快递公司' AFTER tracking_number;
