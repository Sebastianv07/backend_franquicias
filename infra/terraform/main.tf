# Red por defecto de la cuenta (evita crear VPC y subredes para la prueba)
data "aws_vpc" "default" {
  default = true
}

# Firewall de la base de datos
resource "aws_security_group" "mysql" {
  name        = "${var.db_identifier}-sg"
  description = "Acceso a MySQL para la API de franquicias"
  vpc_id      = data.aws_vpc.default.id
}

resource "aws_vpc_security_group_ingress_rule" "mysql" {
  security_group_id = aws_security_group.mysql.id
  description       = "MySQL desde la IP autorizada"
  cidr_ipv4         = var.allowed_cidr
  from_port         = 3306
  to_port           = 3306
  ip_protocol       = "tcp"
}

resource "aws_vpc_security_group_egress_rule" "todo" {
  security_group_id = aws_security_group.mysql.id
  description       = "Permitir todo el trafico de salida"
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1"
}

# Base de datos MySQL administrada
resource "aws_db_instance" "franquicias" {
  identifier     = var.db_identifier
  engine         = "mysql"
  engine_version = "8.4"
  instance_class = var.db_instance_class

  allocated_storage = 20
  storage_type      = "gp2"

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  vpc_security_group_ids = [aws_security_group.mysql.id]
  publicly_accessible    = true
  multi_az               = false

  # Configuración de bajo costo para la prueba: sin backups ni snapshot final
  backup_retention_period = 0
  skip_final_snapshot     = true
  deletion_protection     = false
  apply_immediately       = true
}
