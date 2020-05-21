resource "aws_lb_target_group" "service_target_group" { 
  name = "${replace(local.resource_prefix,"_","-")}-lb-tg"
  port = var.container_port
  protocol = var.protocol
  vpc_id = var.vpc_id
  target_type = "ip"

  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-lb_tg"
  })
}