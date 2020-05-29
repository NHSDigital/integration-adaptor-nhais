resource "aws_lb_target_group" "service_target_group" {
  count       = var.enable_load_balancing ? 1 : 0
  name        = "${replace(local.resource_prefix,"_","-")}-lb-tg"
  port        = var.container_port
  protocol    = var.protocol
  vpc_id      = var.vpc_id
  target_type = "ip"

  health_check {
    enabled  = true
    interval = 30
    path     = var.healthcheck_path
    port     = local.healthcheck_port
    protocol = var.protocol
    timeout  = 30
    matcher = "200-299"
    healthy_threshold   = 30
    unhealthy_threshold = 30
  }

  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-lb_tg"
  })
}