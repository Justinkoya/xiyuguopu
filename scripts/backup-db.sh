#!/bin/bash
# ================================================================
# 西域果铺 — 数据库备份脚本
# 用法: ./backup-db.sh
# 建议: crontab 每天凌晨 2 点执行
#       0 2 * * * /opt/xiyuguopu/scripts/backup-db.sh >> /var/log/xiyuguopu-backup.log 2>&1
# ================================================================

set -e

BACKUP_DIR="${BACKUP_DIR:-/opt/xiyuguopu-data/backups}"
CONTAINER_NAME="${CONTAINER_NAME:-xiyuguopu-db}"
DB_NAME="${DB_NAME:-xiyuguopu}"
DB_USER="${DB_USER:-root}"
RETENTION_DAYS="${RETENTION_DAYS:-7}"

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/${DB_NAME}_${TIMESTAMP}.sql.gz"

mkdir -p "${BACKUP_DIR}"

echo "[$(date '+%Y-%m-%d %H:%M:%S')] 开始备份 ${DB_NAME}..."

# 从容器中 dump 并压缩（密码从环境变量获取，docker exec 默认用 root 无密码）
docker exec "${CONTAINER_NAME}" mariadb-dump \
    -u "${DB_USER}" \
    --single-transaction \
    --quick \
    --routines \
    --triggers \
    "${DB_NAME}" \
    | gzip > "${BACKUP_FILE}"

echo "[$(date '+%Y-%m-%d %H:%M:%S')] 备份完成: ${BACKUP_FILE} ($(du -h "${BACKUP_FILE}" | cut -f1))"

# 清理超过保留天数的旧备份
DELETED=$(find "${BACKUP_DIR}" -name "${DB_NAME}_*.sql.gz" -mtime +${RETENTION_DAYS} -delete -print)
if [ -n "${DELETED}" ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] 清理过期备份: ${DELETED}"
fi

echo "[$(date '+%Y-%m-%d %H:%M:%S')] 当前备份数: $(find "${BACKUP_DIR}" -name "${DB_NAME}_*.sql.gz" | wc -l)"
