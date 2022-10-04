#!/bin/bash
start chrome http://localhost:9090/\#/pod?namespace=workout
kubectl port-forward services/kubernetes-dashboard 9090:443 -n monitoring
