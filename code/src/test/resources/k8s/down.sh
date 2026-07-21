#!/bin/bash
NAMESPACE="dev"

echo "🛑 Deteniendo todos los microservicios y la infraestructura en el namespace '$NAMESPACE'..."

echo "--- Eliminando Microservicios ---"

kubectl delete -f auth/ds-appmic-e-commerceplugins-auth.yaml -n $NAMESPACE
kubectl delete -f maintenance/ds-appmic-e-commerceplugins-maintenance.yaml -n $NAMESPACE
kubectl delete -f orders/ds-appmic-e-commerceplugins-orders.yaml -n $NAMESPACE
kubectl delete -f gateway/ds-appmic-e-commerceplugins-gateway.yaml -n $NAMESPACE

echo "--- Eliminando Base de datos ---"
kubectl delete -f bd/ds-mysql.yaml -n $NAMESPACE