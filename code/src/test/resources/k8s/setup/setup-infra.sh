#!/bin/bash
NAMESPACE="dev"
echo "Creando espacio de trabajo..."
kubectl create namespace $NAMESPACE
echo "🏗️ Configurando Infraestructura Persistente..."
echo "🏗️ Creando mecanismos de seguridad... (RBAC)"
kubectl apply -f rbac.yaml -n $NAMESPACE
echo "🏗️ Creando configuración para los pods... (ConfigMap)"
kubectl apply -f ../configmap/configmap.yaml -n $NAMESPACE
kubectl apply -f ../configmap/configmap-mysql.yaml -n $NAMESPACE
echo "🏗️ Creando configuración para datos sensibles... (Secrets)"
kubectl apply -f ../secret/secret.yaml -n $NAMESPACE
echo "💾 Creando Almacenamiento..."
echo "💾 Creando los PersistentVolumes, estos no requieren un namespace puesto que, son recursos a nivel de clúster... (PV)"
kubectl apply -f ../bd/mysql-pv.yaml
echo "💾 Creando los PersistentVolumesClaim... (PVC)"
kubectl apply -f ../bd/mysql-pvc.yaml -n $NAMESPACE
echo "✅ Infraestructura lista. Los datos ahora persistirán aunque apagues los servicios."
