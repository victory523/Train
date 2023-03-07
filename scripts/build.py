#!/usr/bin/env python3

from pathlib import Path
import sys
root_directory = Path(__file__).parent.parent

sys.path.append(str(root_directory))

from lib.docker_utils import build_and_push_img
from lib.ansible_utils import load_vars

data = load_vars(root_directory / '.ansible/vault_key', root_directory / 'vars/vault.yaml')
docker_username = data['docker_username']
docker_password = data['docker_password']

build_and_push_img(
    src=root_directory / 'client',
    docker_context_path=root_directory / 'client',
    ignore=['node_modules', 'dist'],
    tag_prefix='client',
    image_name='mucsi96/training-log-pro-client',
    docker_username=docker_username,
    docker_password=docker_password
)

build_and_push_img(
    src=root_directory / 'server',
    docker_context_path=root_directory / 'server',
    ignore=['target', 'dist'],
    tag_prefix='server',
    image_name='mucsi96/training-log-pro-server',
    docker_username=docker_username,
    docker_password=docker_password
)
