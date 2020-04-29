resource "aws_ecs_service" "ecs_service" {
  name = "${local.resource_prefix}-service"
  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-service"
  })

  cluster             = var.cluster_id
  task_definition     = aws_ecs_task_definition.ecs_task_definition.arn
  iam_role            = aws_iam_role.service_iam_role.arn
  launch_type         = var.launch_type
  scheduling_strategy = var.scheduling_strategy

  desired_count                      = var.desired_count
  deployment_maximum_percent         = var.deployment_maximum_percent
  deployment_minimum_healthy_percent = var.deployment_minimum_healthy_percent
  health_check_grace_period_seconds  = var.health_check_grace_period_seconds

  load_balancer {
    target_group_arn = aws_lb_target_group.service_target_group.arn
    container_name = "${local.resource_prefix}-container"
    container_port = var.container_port
  }

  network_configuration {
    assign_public_ip = var.assign_public_ip
    security_groups  = merge(var.additional_security_groups,[aws_security_group.service_sg.id])
    subnets          = aws_subnet.service_subnet.*.id
  }
}
