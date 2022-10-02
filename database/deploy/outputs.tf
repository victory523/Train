output "host" {
  value = var.host
}

output "port" {
  value = var.port
}

output "name" {
  value = var.name
}

output "username" {
  value = random_pet.username.id
}

output "password" {
  value = random_password.password.result
}

output "metrics_port" {
  value = var.metrics_port
}
