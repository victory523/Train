#!/bin/bash
pip install -r requirements.txt
ansible-galaxy install -r galaxy-requirements.yaml
rm -rf lib
libs=https://raw.githubusercontent.com/mucsi96/ansible-roles/main/lib
wget $libs/ansible_utils.py --directory-prefix lib
wget $libs/docker_utils.py --directory-prefix lib
wget $libs/version_utils.py --directory-prefix lib