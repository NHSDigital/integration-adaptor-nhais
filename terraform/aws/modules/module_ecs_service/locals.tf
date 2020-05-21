locals {
  resource_prefix = "${var.project}-${var.environment}-${var.module_instance}"
  default_tags = merge(var.default_tags,{
    Module          = var.module_name,
    Module_Instance = var.module_instance,
  })

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