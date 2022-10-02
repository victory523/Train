resource "kubernetes_namespace" "workout" {
  metadata {
    name = "workout"
  }
}

module "client" {
  source     = "./client/deploy"
  namespace  = kubernetes_namespace.workout.metadata.name
  image_name = "mucsi96/${kubernetes_namespace.workout.metadata.name}-client"
}
