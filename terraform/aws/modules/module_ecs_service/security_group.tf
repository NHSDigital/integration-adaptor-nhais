resource "aws_security_group" "service_sg" { 
  name = "${local.resource_prefix}-sg"
}

resource "aws_security_group" "service_lb_sg" {
  name = "${local.resource_prefix}-lb-sg"
  
}