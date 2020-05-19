resource "aws_vpc" "base_vpc" {
  cidr_block = var.base_cidr_block
  tags = merge(local.default_tags,{
    Name = "${local.resource_prefix}-vpc"
  })
}