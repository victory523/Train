terraform {
  backend "kubernetes" {
    secret_suffix = "workout-app-state"
    config_path   = "~/.kube/config"
  }
}
