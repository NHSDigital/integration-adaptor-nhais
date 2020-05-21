data "template_file" "ecs_task_container_definitions" {
  template = file("${path.module}/files/container_definition.json")

  vars = {
    container_name = "${local.resource_prefix}-container"
    image          = var.image_name
    essential               = true
    aws_log_group           = aws_cloudwatch_log_group.ecs_service_cw_log_group.name
    aws_log_region          = var.region
    aws_log_stream_prefix   = var.log_stream_prefix
    aws_log_datetime_format = var.logs_datetime_format
  }
}
    //env_vars       = "[]"
    //secret_vars    = "[]"
    //port_mappings = "[{ containerPort = 80 \n hostPort = 80 \n protocol = \"tcp\"}]"


//before values removed:
/*
[
  {
    "name": "${container_name}",
    "image": "${image}",
    "environment": "${env_vars}",
    "secrets": "${secret_vars}",
    "essential": "${essential}",
    "logConfiguration": {
      "logDriver": "awslogs",
      "options": {
        "awslogs-group":  "${aws_log_group}",
        "awslogs-region": "${aws_log_region}",
        "awslogs-stream-prefix": "${aws_log_stream_prefix}",
        "awslogs-datetime-format": "${aws_log_datetime_format}"
      }
    },
    "portMappings": "${port_mappings}"
  }
]


*/