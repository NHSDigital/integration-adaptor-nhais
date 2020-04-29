variable "project" {
  type = string
  description = "(Required) Name of the project where this module is used"
}

variable "environment" {
  type = string
  description = "(Required) Name of the environment where this modules is used"
}

variable "module_name" {
  type = string
  description = "(Static) Name of this module"
  default = "ecs_service"
}

variable "module_instance" {
  type = string
  description = "(Required) Name of the instance of this module"
}

variable "container_insights" {
  type = string
  default = "disabled"
  description = "(Optional) Container Insights for containers in the cluster, default is disabled"
}