alias ap='ansible-playbook'
alias ave='ansible-vault encrypt vars/vault.yaml inventory.yaml'
alias avd='ansible-vault decrypt vars/vault.yaml inventory.yaml'
alias avv='ansible-vault view vars/vault.yaml '
alias avvi='ansible-vault view inventory.yaml'
alias mt='mvn test'
alias mr='mvn spring-boot:run -P local'
alias dc='docker compose up --build --pull always'

export WORKSPACE_ROOT=$(docker container inspect "$(hostname)" --format='{{range .Mounts}}{{if eq .Destination "/workspaces"}}{{.Source}}{{end}}{{end}}')/training-log-pro
