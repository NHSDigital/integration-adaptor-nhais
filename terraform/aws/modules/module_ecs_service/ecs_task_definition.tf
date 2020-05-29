resource "aws_ecs_task_definition" "ecs_task_definition" { 
  family = "${local.resource_prefix}-task_definition"
  
  task_role_arn = var.task_role_arn
  execution_role_arn = var.task_execution_role_arn
  //container_definitions = data.template_file.ecs_task_container_definitions.rendered
  container_definitions = jsonencode(
    [
      {
        name      = local.container_name
        image     = var.image_name
        #image = "lukaszkw/kainos:feauture-java-local"
        essential = true
        portMappings = local.port_mappings
        logConfiguration = {
          logDriver = "awslogs"
          options = {
            awslogs-group           = aws_cloudwatch_log_group.ecs_service_cw_log_group.name
            awslogs-create-group    = "true"
            awslogs-region          = var.region
            awslogs-stream-prefix   = var.log_stream_prefix
            awslogs-datetime-format = var.logs_datetime_format
          }
        }
        environment = var.environment_variables
        secrets = var.secret_variables
      }
    ]
  )
  # container_definitions = jsonencode(
  #   [
  #     {
  #       name      = local.container_name
  #       image     = "httpd:2.4"
  #       essential = true
  #       portMappings = [
  #         {
  #           containerPort = 80
  #           hostPort = 80
  #           protocol = "tcp"
  #         }
  #       ],
  #       entryPoint = ["sh", "-c"]
  #       command = ["/bin/sh -c \"echo '<html> <head> <title>Amazon ECS Sample App</title> <style>body {margin-top: 40px; background-color: #333;} </style> </head><body> <div style=color:white;text-align:center> <h1>Amazon ECS Sample App</h1> <h2>Congratulations!</h2> <p>Your application is now running on a container in Amazon ECS.</p> </div></body></html>' >  /usr/local/apache2/htdocs/index.html && httpd-foreground\""]
  #       logConfiguration = {
  #         logDriver = "awslogs"
  #         options = {
  #           awslogs-group           = aws_cloudwatch_log_group.ecs_service_cw_log_group.name
  #           awslogs-create-group    = "true"
  #           awslogs-region          = var.region
  #           awslogs-stream-prefix   = var.log_stream_prefix
  #           awslogs-datetime-format = var.logs_datetime_format
  #         }
  #       },
  #       environment = var.environment_variables
  #     }
  #   ]
  # )

  cpu = var.cpu_units
  memory = var.memory_units
  network_mode = var.network_mode
  requires_compatibilities = [var.launch_type]

  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-task_definition"
  })
}