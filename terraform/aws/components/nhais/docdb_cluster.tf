resource "aws_docdb_cluster" "nhais_db_cluster" {
  cluster_identifier = "${replace(local.resource_prefix,"_","-")}-dbcluster"
  engine = "docdb"
  master_username = var.docdb_master_user
  master_password = var.docdb_master_password
  backup_retention_period = 1
  skip_final_snapshot = true
  db_subnet_group_name = aws_docdb_subnet_group.nhais_db_subnet_group.name
  vpc_security_group_ids = [aws_security_group.docdb_sg.id]

  tags = merge(local.default_tags,{
    Name = "${local.resource_prefix}-dbcluster"
  })
}
