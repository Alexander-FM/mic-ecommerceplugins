#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "🧹 Eliminando contenedores, imágenes locales y volúmenes..."
docker compose -f "$ROOT_DIR/docker-compose.yml" down -v --remove-orphans --rmi local

echo "✅ Limpieza completa finalizada."

