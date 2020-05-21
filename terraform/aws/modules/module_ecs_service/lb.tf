resource "aws_lb" "service_load_balancer" {
  name = "${replace(local.resource_prefix,"_","-")}-lb"
  internal = true
  load_balancer_type = var.load_balancer_type
  security_groups = [aws_security_group.service_lb_sg.id]
  subnets = aws_subnet.service_subnet.*.id

  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-lb"
  })
}