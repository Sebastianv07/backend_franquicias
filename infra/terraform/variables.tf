variable "aws_region" {
  description = "Región de AWS donde se crea la base de datos"
  type        = string
  default     = "us-east-1"
}

variable "db_identifier" {
  description = "Nombre de la instancia RDS"
  type        = string
  default     = "franquicias-db"
}

variable "db_name" {
  description = "Nombre de la base de datos"
  type        = string
  default     = "franquicias_db"
}

variable "db_username" {
  description = "Usuario administrador de la base de datos"
  type        = string
  default     = "franquicias_user"
}

variable "db_password" {
  description = "Contraseña del usuario administrador"
  type        = string
  sensitive   = true

  validation {
    condition     = length(var.db_password) >= 8 && can(regex("^[^/@\" ]+$", var.db_password))
    error_message = "La contraseña debe tener al menos 8 caracteres y no puede contener /, @, comillas dobles ni espacios."
  }
}

variable "db_instance_class" {
  description = "Tamaño de la instancia RDS"
  type        = string
  default     = "db.t4g.micro"
}

variable "allowed_cidr" {
  description = "IP pública (formato x.x.x.x/32) autorizada a conectarse a MySQL"
  type        = string
}
