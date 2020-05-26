output "vpc_id" {
  value = aws_vpc.base_vpc.id
}

output "base_cluster_id" {
  value = module.base_ecs_cluster.cluster_id
}

output "base_cluster_name" {
  value = module.base_ecs_cluster.cluster_name
}

output "vpc_cidr" {
  value = aws_vpc.base_vpc.cidr_block
}

output "core_sg_id" {
  value = aws_security_group.core_sg.id
}

output "cloudwatch_vpce_id" {
  value = aws_vpc_endpoint.cloudwatch_endpoint.id
}

output "ecr_vpce_id" {
  value = aws_vpc_endpoint.ecr_endpoint.id
}