resource "aws_docdb_cluster_parameter_group" "nhais_db_parameters" {
  name = "${replace(local.resource_prefix,"_","-")}-db-parameters-36"
  family = "docdb3.6"
  description = "Parameter group for MongoDB in env: ${var.environment}"

  parameter {
    name = "tls"
    value = "disabled"
    apply_method = "immediate"
  }

  parameter {
    name = "audit_logs"
    value = "enabled"
    apply_method = "immediate"
  }

  tags = merge(local.default_tags,{
    Name = "${replace(local.resource_prefix,"_","-")}-db-parameters-36"
  })
}