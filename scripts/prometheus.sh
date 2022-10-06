#!/bin/bash
start chrome http://localhost:9095/
kubectl port-forward --namespace monitoring services/kube-prometheus-stack-prometheus 9095:http-web
