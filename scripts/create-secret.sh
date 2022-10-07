#!/bin/bash
echo -n "Secret: "
read SECRET
echo -n $SECRET | kubeseal --raw --scope cluster-wide | clip
echo "Your sealed secret is in clipboard."