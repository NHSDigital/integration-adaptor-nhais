resource "aws_ecs_task_definition" "ecs_task_definition" { 
  family = "${local.resource_prefix}-task_definition"
  
  task_role_arn = data.aws_iam_role.ecs_service_task_role.arn
  execution_role_arn = data.aws_iam_role.ecs_service_task_execution_role.arn
  //container_definitions = data.template_file.ecs_task_container_definitions.rendered
  container_definitions = jsonencode(
    [
      {
        name      = "${local.resource_prefix}-container"
        image     = var.image_name
        essential = true
        portMappings = [
          {
            containerPort = 80
            hostPort = 80
            protocol = "tcp"
          }
        ],
        logConfiguration = {
          logDriver = "awslogs"
          options = {
            awslogs-group           = aws_cloudwatch_log_group.ecs_service_cw_log_group.name
            awslogs-region          = var.region
            awslogs-stream-prefix   = var.log_stream_prefix
            awslogs-datetime-format = var.logs_datetime_format
          }
        },
        environment = var.environment_variables
      }
    ]
  )

  cpu = var.cpu_units
  memory = var.memory_units
  network_mode = var.network_mode
  requires_compatibilities = [var.launch_type]

    tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-task_definition"
  })
}