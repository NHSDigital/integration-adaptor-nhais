resource "aws_security_group" "cloudwatch_sg" { 
  name = "${local.resource_prefix}-cloudwatch_sg"
}

resource "aws_security_group" "ecr_sg" {
    name = "${local.resource_prefix}-ecr_sg"
}