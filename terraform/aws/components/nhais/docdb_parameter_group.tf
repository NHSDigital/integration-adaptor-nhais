resource "aws_docdb_cluster_parameter_group" "nhais_db_parameters" {
  name = "${local.resource_prefix}-db-parameters-3.6"
  family = "docdb3.6"
  description = "Parameter group for MongoDB in env: ${var.environment}"

  parameter {
    name = "tls"
    value = "disabled"
  }

  parameter {
    name = "audit_logs"
    value = "enabled"
  }

  tags = merge(local.default_tags,{
    Name = "${local.resource_prefix}-db-parameters-3.6"
  })
}