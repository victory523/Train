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

module "database" {
  source      = "./database/deploy"
  namespace   = local.namespace
  name        = local.namespace
  init_db_sql = file("./server/src/main/resources/schema.sql")
}
