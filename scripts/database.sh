#!/bin/bash
kubectl port-forward services/app-database 3308:3306 -n workout
