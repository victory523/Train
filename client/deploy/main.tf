module "image_version" {
  source     = "../version"
  tag_prefix = "client-image"
  path       = ".."
  ignore     = ["deploy/"]
}

module "chart_version" {
  source      = "../helm-chart-version"
  app_version = module.image_version.version
  tag_prefix  = "client-chart"
  path        = "."
}

resource "null_resource" "image" {
  provisioner "local-exec" {
    command = <<-EOT
      docker build .. \
        --quiet \
        --tag ${var.image_name}:${module.image_version.version} \
        --tag ${var.image_name}:latest
      docker push ${var.image_name} --all-tags
      echo "New image published ${var.image_name}:${module.image_version.version}"
    EOT
  }

  triggers = {
    version = module.image_version.version
  }
}

resource "helm_release" "chart" {
  name             = var.host
  version          = module.chart_version.version
  namespace        = var.namespace
  create_namespace = true
  chart            = "."
  depends_on = [
    resource.null_resource.image
  ]

  set {
    name  = "service.port"
    value = var.port
  }

  set {
    name  = "service.metricsPort"
    value = var.metrics_port
  }
}
