#!/bin/bash
start chrome http://localhost:9094/wallboard
kubectl port-forward services/spring-boot-admin-server 9094:9090 -n monitoring
