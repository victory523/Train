alias ap='ansible-playbook'
alias ave='ansible-vault encrypt vars/vault.yaml inventory.yaml'
alias avd='ansible-vault decrypt vars/vault.yaml inventory.yaml'
alias avv='ansible-vault view vars/vault.yaml '
alias avvi='ansible-vault view inventory.yaml'
alias mt='mvn test'
alias mr='mvn spring-boot:run -P local'
alias dcu='node docker-compose.js | docker compose --file - up --force-recreate --build --remove-orphans --wait --pull always'
alias dcd='node docker-compose.js | docker compose --file - down'
alias dump='pg_dump --dbname postgresql://postgres:postgres@db/training-log --inserts --data-only >> insert.sql'

export WORKSPACE_ROOT=$(docker container inspect "$(hostname)" --format='{{range .Mounts}}{{if eq .Destination "/workspaces"}}{{.Source}}{{end}}{{end}}')/training-log-pro
export DOCKER_NETWORK=$(docker container inspect "$(hostname)" --format='{{range $key,$value := .NetworkSettings.Networks}}{{$key}}{{end}}')
export TESTCONTAINERS_RYUK_DISABLED=true
