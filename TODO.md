- Run mucsi96.kubetools.pull_kube_config role only on local machine. Not on CI
- Remove filter in favor of /withings/authenticate endpoint
- Migrate AuthorizedClientRepository to JPA
- Create database for stats
- Sync stats in `/status`
- Create scheduled db backups


- Build images as part of pipeline https://docs.github.com/en/actions/publishing-packages/publishing-docker-images#publishing-images-to-github-packages
https://stackoverflow.com/questions/70456385/how-to-publish-changes-to-docker-images-using-github-actions
