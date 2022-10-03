variable "namespace" {
  type = string
}

variable "image_name" {
  type = string
}

variable "host" {
  type    = string
  default = "app-server"
}

variable "port" {
  type    = number
  default = 8080
}

variable "management_port" {
  type    = number
  default = 8082
}

variable "database" {
  type = object({
    name     = string,
    host     = string,
    port     = number,
    username = string,
    password = string
  })
}

variable "admin_server" {
  type = object({
    host = string
    port = number
  })
}
