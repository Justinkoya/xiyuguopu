#!/usr/bin/env bash
set -Eeuo pipefail

REPO_URL="${REPO_URL:-https://github.com/Justinkoya/xiyuguopu.git}"
BRANCH="${BRANCH:-codex/server-ip-deploy}"
APP_DIR="${APP_DIR:-/opt/xiyuguopu}"
PUBLIC_HOST="${PUBLIC_HOST:-47.109.93.162}"
NGINX_PORT="${NGINX_PORT:-80}"

log() {
  printf '\n[%s] %s\n' "$(date '+%Y-%m-%d %H:%M:%S')" "$*"
}

die() {
  printf '\nERROR: %s\n' "$*" >&2
  exit 1
}

need_root() {
  if [ "$(id -u)" -ne 0 ]; then
    die "Please run as root, for example: sudo bash scripts/deploy.sh"
  fi
}

pkg_install() {
  if command -v dnf >/dev/null 2>&1; then
    dnf install -y "$@"
  elif command -v yum >/dev/null 2>&1; then
    yum install -y "$@"
  else
    die "dnf/yum not found. This script targets Alibaba Cloud Linux/RHEL-like systems."
  fi
}

ensure_packages() {
  log "Installing required packages"
  pkg_install git podman podman-docker podman-compose curl openssl

  if command -v systemctl >/dev/null 2>&1; then
    systemctl enable --now podman.socket >/dev/null 2>&1 || true
  fi
}

ensure_repo() {
  if [ -d "$APP_DIR/.git" ]; then
    log "Updating existing repo at $APP_DIR"
    git -C "$APP_DIR" fetch origin "$BRANCH"
    git -C "$APP_DIR" checkout "$BRANCH"
    git -C "$APP_DIR" pull --ff-only origin "$BRANCH"
  else
    log "Cloning repo to $APP_DIR"
    mkdir -p "$(dirname "$APP_DIR")"
    git clone -b "$BRANCH" "$REPO_URL" "$APP_DIR"
  fi
}

random_secret() {
  openssl rand -base64 48 | tr -d '\n'
}

ensure_env() {
  cd "$APP_DIR"

  if [ -f .env ]; then
    log ".env already exists, keeping it unchanged"
    return
  fi

  log "Creating .env with generated passwords"
  umask 077
  cat > .env <<EOF_ENV
MYSQL_ROOT_PASSWORD=$(random_secret)
MYSQL_PORT=3306
MYSQL_DATABASE=xiyuguopu
MYSQL_USERNAME=xiyuguopu
MYSQL_PASSWORD=$(random_secret)
XIYU_JWT_SECRET=$(random_secret)
APP_CORS_ALLOWED_ORIGINS=http://${PUBLIC_HOST}
NGINX_PORT=${NGINX_PORT}
EOF_ENV
}

compose() {
  if command -v podman-compose >/dev/null 2>&1; then
    podman-compose "$@"
  elif command -v docker-compose >/dev/null 2>&1; then
    docker-compose "$@"
  elif command -v docker >/dev/null 2>&1 && docker compose version >/dev/null 2>&1; then
    docker compose "$@"
  else
    die "No compose command found."
  fi
}

start_stack() {
  cd "$APP_DIR"
  log "Starting containers"
  compose up -d --build
}

show_status() {
  cd "$APP_DIR"
  log "Container status"
  compose ps

  log "Recent server logs"
  compose logs --tail=80 server || true
}

health_check() {
  log "Checking local API"
  for i in $(seq 1 30); do
    if curl -fsS "http://127.0.0.1:${NGINX_PORT}/api/products" >/tmp/xiyuguopu-health.json; then
      cat /tmp/xiyuguopu-health.json
      printf '\n'
      log "Deploy finished: http://${PUBLIC_HOST}/"
      log "Admin: http://${PUBLIC_HOST}/admin/"
      return
    fi
    sleep 3
  done

  log "Health check failed. Useful logs:"
  cd "$APP_DIR"
  compose logs --tail=120 db || true
  compose logs --tail=120 server || true
  compose logs --tail=120 nginx || true
  die "API did not respond on http://127.0.0.1:${NGINX_PORT}/api/products"
}

main() {
  need_root
  ensure_packages
  ensure_repo
  ensure_env
  start_stack
  show_status
  health_check
}

main "$@"
