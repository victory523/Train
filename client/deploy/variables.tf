variable "namespace" {
  type = string
}

variable "image_name" {
  type = string
}

variable "host" {
  type    = string
  default = "app-client"
}

variable "port" {
  type    = number
  default = 80
}

variable "metrics_port" {
  type    = number
  default = 8085
}
