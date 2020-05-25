resource "aws_docdb_cluster_instance" "nhais_db_instance" {
  count = 1
  identifier = "${replace(local.resource_prefix,"_","-")}-dbinstance-${count.index}"
  cluster_identifier = aws_docdb_cluster.nhais_db_cluster.id
  instance_class = "db.r4.large"
  apply_immediately = true
  availability_zone = local.availability_zones[count.index]
  
  tags = merge(local.default_tags,{
    Name = "${local.resource_prefix}-dbinstance-${count.index}"
  })
}