resource "aws_security_group" "service_sg" { 
  name = "${local.resource_prefix}-sg"
}