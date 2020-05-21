resource "aws_security_group" "service_sg" { 
  name = "${local.resource_prefix}-sg"
  vpc_id = var.vpc_id
  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-sg"
  })
}

resource "aws_security_group" "service_lb_sg" {
  name = "${local.resource_prefix}-lb_sg"
  vpc_id = var.vpc_id
  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-lb_sg"
  })
}
