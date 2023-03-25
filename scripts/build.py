#!/usr/bin/env python3

from pathlib import Path
import sys
from kubetools.docker_utils import build_and_push_client_img, build_and_push_server_img
from kubetools.ansible_utils import load_vars

root_directory = Path(__file__).parent.parent
data = load_vars(root_directory / '.ansible/vault_key', root_directory / 'vars/vault.yaml')
docker_username = data['docker_username']
docker_password = data['docker_password']
github_access_token = sys.argv[1]

if not github_access_token:
    print('GitHub access token is missing', flush=True, file=sys.stderr)
    exit(1)

build_and_push_client_img(
    src=root_directory / 'client',
    tag_prefix='client',
    image_name='mucsi96/training-log-pro-client',
    docker_username=docker_username,
    docker_password=docker_password,
    github_access_token=github_access_token
)

build_and_push_server_img(
    src=root_directory / 'server',
    tag_prefix='server',
    image_name='mucsi96/training-log-pro-server',
    docker_username=docker_username,
    docker_password=docker_password,
    github_access_token=github_access_token
)
