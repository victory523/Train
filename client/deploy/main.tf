module "image_version" {
  source     = "github.com/mucsi96/terraform-modules//version"
  tag_prefix = "client-image"
  path       = "${path.module}/.."
  ignore     = ["deploy/"]
}

module "chart_version" {
  source      = "github.com/mucsi96/terraform-modules//helm-chart-version"
  app_version = module.image_version.version
  tag_prefix  = "client-chart"
  path        = path.module
}

resource "null_resource" "image" {
  provisioner "local-exec" {
    command = <<-EOT
      docker build ${path.module}/.. \
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
  name      = var.host
  version   = module.chart_version.version
  namespace = var.namespace
  chart     = path.module
  depends_on = [
    resource.null_resource.image
  ]

  set {
    name  = "image.repository"
    value = image_name
  }

  set {
    name  = "service.port"
    value = var.port
  }

  set {
    name  = "service.metricsPort"
    value = var.metrics_port
  }
}
