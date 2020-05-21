resource "aws_subnet" "service_subnet" {
  count = length(var.subnet_cidrs)
  vpc_id = var.vpc_id
  cidr_block = var.subnet_cidrs[count.index]
  availability_zone = var.availability_zones[count.index]

  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-subnet-${count.index}"
  })
}

# resource "aws_subnet" "load_balancer_subnet" { 
  
# }

# resource "aws_subnet" "containers_subnet" { 
  
# }
