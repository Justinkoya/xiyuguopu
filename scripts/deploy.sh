#!/usr/bin/env bash
set -Eeuo pipefail

REPO_URL="${REPO_URL:-https://github.com/Justinkoya/xiyuguopu.git}"
GIT_MIRROR_PREFIXES="${GIT_MIRROR_PREFIXES:-}"
ALLOW_DIRECT_DOWNLOADS="${ALLOW_DIRECT_DOWNLOADS:-1}"
CONTAINER_IMAGE_MIRRORS="${CONTAINER_IMAGE_MIRRORS:-dockerproxy.net docker.1panel.live}"
BRANCH="${BRANCH:-codex/admin-image-upload}"
APP_DIR="${APP_DIR:-/opt/xiyuguopu}"
PUBLIC_HOST="${PUBLIC_HOST:-47.109.93.162}"
NGINX_PORT="${NGINX_PORT:-80}"
UPLOAD_IMAGE_DIR="${UPLOAD_IMAGE_DIR:-/opt/xiyuguopu-data/uploads/images}"

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

repo_urls() {
  local prefix
  for prefix in $GIT_MIRROR_PREFIXES; do
    printf '%s%s\n' "$prefix" "$REPO_URL"
  done
  if [ "$ALLOW_DIRECT_DOWNLOADS" = "1" ]; then
    printf '%s\n' "$REPO_URL"
  fi
}

ensure_repo() {
  if [ -d "$APP_DIR/.git" ]; then
    log "Updating existing repo at $APP_DIR"
    local url
    for url in $(repo_urls); do
      log "Fetching $BRANCH via $url"
      if git -C "$APP_DIR" fetch "$url" "$BRANCH"; then
        if git -C "$APP_DIR" rev-parse --verify "$BRANCH" >/dev/null 2>&1; then
          git -C "$APP_DIR" switch "$BRANCH"
          git -C "$APP_DIR" pull --ff-only "$url" "$BRANCH"
        else
          git -C "$APP_DIR" switch -c "$BRANCH" FETCH_HEAD
        fi
        return
      fi
    done
    die "Failed to fetch branch $BRANCH from configured Git mirrors."
  else
    log "Cloning repo to $APP_DIR"
    mkdir -p "$(dirname "$APP_DIR")"
    local url
    for url in $(repo_urls); do
      log "Cloning $BRANCH via $url"
      if git clone -b "$BRANCH" "$url" "$APP_DIR"; then
        return
      fi
    done
    die "Failed to clone branch $BRANCH from configured Git mirrors."
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
UPLOAD_IMAGE_DIR=${UPLOAD_IMAGE_DIR}
EOF_ENV
}

ensure_upload_dir() {
  log "Preparing upload directory: $UPLOAD_IMAGE_DIR"
  mkdir -p "$UPLOAD_IMAGE_DIR"
  chmod 0775 "$UPLOAD_IMAGE_DIR"
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

container_cli() {
  if command -v podman >/dev/null 2>&1; then
    printf 'podman\n'
  elif command -v docker >/dev/null 2>&1; then
    printf 'docker\n'
  else
    die "podman/docker not found."
  fi
}

pull_image_from_mirrors() {
  local image="$1"
  local target="$2"
  local cli mirror source
  cli="$(container_cli)"

  if "$cli" image exists "$target" >/dev/null 2>&1; then
    log "Image already exists: $target"
    return
  fi

  for mirror in $CONTAINER_IMAGE_MIRRORS; do
    source="${mirror}/${image}"
    log "Pulling $target via $source"
    if "$cli" pull "$source"; then
      "$cli" tag "$source" "$target"
      return
    fi
  done

  if [ "$ALLOW_DIRECT_DOWNLOADS" = "1" ]; then
    log "Pulling $target directly"
    "$cli" pull "$target"
    return
  fi

  die "Failed to pull $target from configured container mirrors."
}

prepull_images() {
  pull_image_from_mirrors library/maven:3.9.9-eclipse-temurin-17 maven:3.9.9-eclipse-temurin-17
  pull_image_from_mirrors library/eclipse-temurin:17-jre eclipse-temurin:17-jre
  pull_image_from_mirrors library/mariadb:10.11 mariadb:10.11
  pull_image_from_mirrors library/nginx:1.26-alpine nginx:1.26-alpine
}

start_stack() {
  cd "$APP_DIR"
  log "Preparing base images from China mirrors"
  prepull_images
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
  ensure_upload_dir
  start_stack
  show_status
  health_check
}

main "$@"
