#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "🏗️ Validando y construyendo imágenes..."
docker compose -f "$ROOT_DIR/docker-compose.yml" config >/dev/null
docker compose -f "$ROOT_DIR/docker-compose.yml" build

echo "✅ Setup listo. Ejecuta scripts/up.sh para levantar el stack."

