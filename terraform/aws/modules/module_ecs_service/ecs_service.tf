resource "aws_ecs_service" "ecs_service" {
  name = "${local.resource_prefix}-service"

  cluster             = var.cluster_id
  task_definition     = aws_ecs_task_definition.ecs_task_definition.arn
  //iam_role            = aws_iam_role.service_iam_role.arn
  launch_type         = var.launch_type
  scheduling_strategy = var.scheduling_strategy

  desired_count                      = var.desired_count
  deployment_maximum_percent         = var.deployment_maximum_percent
  deployment_minimum_healthy_percent = var.deployment_minimum_healthy_percent
  health_check_grace_period_seconds  = var.enable_load_balancing ? var.health_check_grace_period_seconds : null


  dynamic "load_balancer" {
    for_each = local.load_balancer_settings
    content {
      target_group_arn = load_balancer.target_group_arn
      container_name = load_balancer.container_name
      container_port = load_balancer.container_port
    }
  }

  # load_balancer {
  #   target_group_arn = aws_lb_target_group.service_target_group.arn
  #   container_name = "${local.resource_prefix}-container"
  #   container_port = var.container_port
  # }

  network_configuration {
    assign_public_ip = var.assign_public_ip
    security_groups  = concat(var.additional_security_groups,[aws_security_group.service_sg.id])
    subnets          = aws_subnet.service_subnet.*.id
  }
  # Tags may not yet be supported - TODO
  # tags = merge(local.default_tags, {
  #   Name = "${local.resource_prefix}-service"
  # })
}
