output "db_endpoint" {
  description = "Host de la base de datos"
  value       = aws_db_instance.franquicias.address
}

output "db_port" {
  description = "Puerto de la base de datos"
  value       = aws_db_instance.franquicias.port
}

output "db_url" {
  description = "URL JDBC para la variable DB_URL de la aplicación"
  value       = "jdbc:mysql://${aws_db_instance.franquicias.address}:${aws_db_instance.franquicias.port}/${var.db_name}"
}
