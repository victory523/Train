locals {
  namespace = "workout"
}

module "client" {
  source     = "./client/deploy"
  namespace  = local.namespace
  image_name = "mucsi96/${local.namespace}-client"
}
