resource "aws_docdb_cluster" "nhais_db_cluster" {
  cluster_identifier = "${local.resource_prefix}-db_cluster"
  engine = "docdb"
  master_username = "master_user"
  master_password = "master567"
  backup_retention_period = 1
  skip_final_snapshot = true
  db_subnet_group_name = aws_docdb_subnet_group.nhais_db_subnet_group.name
  vpc_security_group_ids = [aws_security_group.docdb_sg.id]

  tags = merge(local.default_tags,{
    Name = "${local.resource_prefix}-db_cluster"
  })
}
