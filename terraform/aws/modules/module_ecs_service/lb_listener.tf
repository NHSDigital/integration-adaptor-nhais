resource "aws_lb_listener" "service_listener" {
  load_balancer_arn = aws_lb.service_load_balancer.arn
  port              = var.container_port
  protocol          = var.protocol

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.service_target_group.arn
  }
}