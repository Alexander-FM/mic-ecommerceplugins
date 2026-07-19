#!/bin/bash

# --- INICIO DE LA CORRECCIÓN ---
# Obtiene la ruta absoluta del directorio donde se encuentra el script.
# Esto hace que el script se pueda ejecutar desde cualquier ubicación.
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

# La ruta base para todas las carpetas (configmap, secret, etc.) es el directorio padre del script.
BASE_DIR=$(dirname "$SCRIPT_DIR")
# --- FIN DE LA CORRECCIÓN ---

NAMESPACE="dev"
echo "🏗️ Configurando Infraestructura Persistente..."

echo "🏗️ Creando mecanismos de seguridad... (RBAC)"
kubectl apply -f "$BASE_DIR/setup/rbac.yaml" -n $NAMESPACE

echo "🏗️ Creando configuración para los pods... (ConfigMap)"
kubectl apply -f "$BASE_DIR/configmap/configmap.yaml" -n $NAMESPACE
kubectl apply -f "$BASE_DIR/configmap/configmap-mysql.yaml" -n $NAMESPACE

echo "🏗️ Creando configuración para datos sensibles... (Secrets)"
kubectl apply -f "$BASE_DIR/secret/secret.yaml" -n $NAMESPACE

echo "💾 Creando Almacenamiento..."
echo "💾 Creando los PersistentVolumes, estos no requieren un namespace puesto que, son recursos a nivel de clúster... (PV)"
kubectl apply -f "$BASE_DIR/bd/mysql-pv.yaml"

echo "💾 Creando los PersistentVolumesClaim... (PVC)"
kubectl apply -f "$BASE_DIR/bd/mysql-pvc.yaml" -n $NAMESPACE

echo "✅ Infraestructura lista. Los datos ahora persistirán aunque apagues los servicios."
