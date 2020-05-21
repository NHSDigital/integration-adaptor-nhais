data "template_file" "ecs_task_container_definitions" {
  template = file("${path.module}/files/container_definition.json")

  vars {
    container_name = "${local.resource_prefix}-container"
    image          = var.image_name
    //env_vars       =  "[${join(","'local.env_vars_list)}]"
    env_vars       = []
    secret_vars    = []
    essential      = true
    aws_log_group  = aws_log_group.ecs_service_cw_log_group.name
    aws_log_region = var.region
    aws_log_stream_prefix = var.log_stream_prefix
    aws_log_datetime_format = var.logs_datetime_format
    port_mappings = ["{ containerPort = 80 \n hostPort = 80 \n protocol = \"tcp\"}"]
  }
}
