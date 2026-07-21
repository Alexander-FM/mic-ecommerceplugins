#!/bin/bash
NAMESPACE="dev"

echo "🛑 Deteniendo todos los microservicios y la infraestructura en el namespace '$NAMESPACE'..."

echo "--- Eliminando Microservicios ---"

kubectl delete -f ../auth/ds-appmic-e-commerceplugins-auth.yaml -n $NAMESPACE
kubectl delete -f ../maintenance/ds-appmic-e-commerceplugins-maintenance.yaml -n $NAMESPACE
kubectl delete -f ../orders/ds-appmic-e-commerceplugins-orders.yaml -n $NAMESPACE
kubectl delete -f ../gateway/ds-appmic-e-commerceplugins-gateway.yaml -n $NAMESPACE

echo "--- Eliminando Base de datos ---"
kubectl delete -f ../bd/ds-mysql.yaml -n $NAMESPACE

echo "--- Eliminando Secretos y Configmap y Almacenamiento"
kubectl delete -f ../secret/secret.yaml -n $NAMESPACE
kubectl delete -f ../configmap/configmap.yaml -n $NAMESPACE
kubectl delete -f ../configmap/configmap-mysql.yaml -n $NAMESPACE
kubectl delete -f ../bd/mysql-pvc.yaml -n $NAMESPACE
# Los PVs no tienen namespace, así que se eliminan sin -n
kubectl delete -f ../bd/mysql-pv.yaml

echo "--- Eliminando el Cluster role binding RBAC ---"
kubectl delete -f ../setup/rbac.yaml

echo "✅ Limpieza completada en el namespace '$NAMESPACE'."
echo "Para eliminar el namespace por completo, ejecuta: kubectl delete namespace $NAMESPACE"