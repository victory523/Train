#!/usr/bin/env python3

from pathlib import Path
from lib.ansible_utils import create_vault_key

root_directory = Path(__file__).parent.parent

create_vault_key(root_directory / '.ansible/vault_key')