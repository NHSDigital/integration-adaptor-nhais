locals {
  resource_prefix = "${var.project}-${var.environment}-${var.module_instance}"
  default_tags = {
    Project         = var.project,
    Environment     = var.environment,
    Module          = var.module_name,
    Module_Instance = var.module_instance,
  }
}