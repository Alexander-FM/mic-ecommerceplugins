#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "🛑 Deteniendo contenedores..."
docker compose -f "$ROOT_DIR/docker-compose.yml" down --remove-orphans

echo "✅ Contenedores detenidos. La base persiste en el volumen mysql-data."

