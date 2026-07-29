#!/bin/bash
NAMESPACE="dev"
echo "🚀 Iniciando despliegue del ecosistema CARRITO DE COMPRAS..."

# 1. BASES DE DATOS
echo "🗄️ Desplegando Bases de Datos..."
kubectl apply -f bd/ds-mysql.yaml -n $NAMESPACE
echo "⏳ Esperando estabilidad de Bases de Datos..."
kubectl rollout status deployment/mysql -n $NAMESPACE

echo "🚀 Iniciando SECUENCIA DE MICROSERVICIOS..."

echo "--- Desplegando AUTH ---"
kubectl apply -f auth/ds-appmic-e-commerceplugins-auth.yaml -n $NAMESPACE
kubectl rollout status deployment/appmic-e-commerceplugins-auth -n $NAMESPACE

echo "--- Desplegando MAINTENANCE ---"
kubectl apply -f maintenance/ds-appmic-e-commerceplugins-maintenance.yaml -n $NAMESPACE
kubectl rollout status deployment/appmic-e-commerceplugins-maintenance -n $NAMESPACE

echo "--- Desplegando ORDERS ---"
kubectl apply -f orders/ds-appmic-e-commerceplugins-orders.yaml -n $NAMESPACE
kubectl rollout status deployment/appmic-e-commerceplugins-orders -n $NAMESPACE

echo "--- Desplegando GATEWAY ---"
kubectl apply -f gateway/ds-appmic-e-commerceplugins-gateway.yaml -n $NAMESPACE
kubectl rollout status deployment/appmic-e-commerceplugins-gateway -n $NAMESPACE

echo "--- Desplegando FRONTEND ---"
#kubectl apply -f frontend/ds-ecommercefrontend.yaml -n $NAMESPACE
#kubectl rollout status deployment/ecommerce-frontend -n $NAMESPACE

echo "✅ ¡Todo el ecosistema ECOMMERCE está arriba!"