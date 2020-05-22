locals {
  resource_prefix = "${var.project}-${var.environment}-${var.module_instance}"
  default_tags = merge(var.default_tags,{
    Module          = var.module_name,
    Module_Instance = var.module_instance,
  })

  load_balancer_default_settings = {
    default = {
      target_group_arn = aws_lb_target_group.service_target_group.arn
      container_name = "${local.resource_prefix}-container"
      container_port = var.container_port
    }
  }

  load_balancer_settings = var.enable_load_balancing ? local.load_balancer_default_settings : {}

  # env_vars_list = [
  #   for e in var.environment_variables : "\{ name = 'e.key' value = e.value \}"
  # ]
}


/*
        {
          name = "MHS_LOG_LEVEL"
          value = var.mhs_log_level
        },

        */