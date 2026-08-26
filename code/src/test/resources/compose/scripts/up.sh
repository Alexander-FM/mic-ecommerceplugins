#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "🚀 Levantando servicios con Docker Compose..."
docker compose -f "$ROOT_DIR/docker-compose.yml" up -d

echo "✅ Stack arriba. Gateway: http://localhost:8080"

