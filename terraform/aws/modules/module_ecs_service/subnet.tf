resource "aws_subnet" "service_subnet" {
  count = length(var.subnet_cidrs)
  name = "${local.resource_prefix}-subnet-${count.index}"
  vpc_id = var.vpc_id
  cidr_block = var.subnet_cidrs[count.index]
  
}