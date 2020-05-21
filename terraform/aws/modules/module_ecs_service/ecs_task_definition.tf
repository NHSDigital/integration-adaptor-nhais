resource "aws_ecs_task_definition" "ecs_task_definition" { 
  family = "${local.resource_prefix}-task_definition"
  
  task_role_arn = data.aws_iam_role.ecs_service_task_role.arn
  execution_role_arn = data.aws_iam_role.ecs_service_task_execution_role.arn
  container_definitions = data.template_file.ecs_task_container_definitions.rendered

  cpu = var.cpu_units
  memory = var.memory_units
  network_mode = var.network_mode
  requires_compatibilities = [var.launch_type]

    tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-task_definition"
  })
}