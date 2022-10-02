locals {
  namespace = "workout"
}

resource "kubernetes_namespace" "workout" {
  metadata {
    name = local.namespace
  }
}

module "client" {
  source     = "./client/deploy"
  namespace  = local.namespace
  image_name = "mucsi96/${local.namespace}-client"
}
