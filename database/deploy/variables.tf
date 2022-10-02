variable "namespace" {
  type = string
}

variable "host" {
  type    = string
  default = "app-database"
}

variable "port" {
  type    = number
  default = 3306
}

variable "name" {
  type = string
}

variable "metrics_port" {
  type    = number
  default = 8085
}

variable "init_db_sql" {
  type    = string
  default = ""
}
