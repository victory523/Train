#!/bin/bash
username=$(kubectl get secret app-database -n workout -o jsonpath="{.data.username}" | base64 --decode)
echo "Username: $username"
kubectl port-forward services/app-database 3308:http -n workout
