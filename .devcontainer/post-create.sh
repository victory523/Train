#!/bin/bash
pip install -r requirements.txt
ansible-galaxy install -r galaxy-requirements.yaml

cd client
yarn
