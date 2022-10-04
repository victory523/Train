#!/bin/bash
POD_NAME=$(kubectl get pods --namespace kube-system --selector app.kubernetes.io/name=traefik | awk '{print $1}' | grep traefik)
start chrome http://localhost:9000/dashboard/
kubectl port-forward $POD_NAME 9000:9000 -n kube-system
