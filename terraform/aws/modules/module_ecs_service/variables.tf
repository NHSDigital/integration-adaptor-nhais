variable "project" {
  type = string
  description = "(Required) Name of the project where this module is used"
}

variable "environment" {
  type = string
  description = "(Required) Name of the environment where this modules is used"
}

variable "component" {
  type = string
  description = "Name of the component where the module is used"
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

variable "cluster_id" {
  type = string
  description = "(Required) ID of the cluster to run the service on"
}

variable "desired_count" {
  type = number
  description = "(Required) Number of containers to run in the service"
}

variable "container_port" {
  type = number
  description = "(Required) Port Number on which service within container will be listening"
}

variable "health_check_grace_period" {
  type = number
  description = "(Optional) Seconds to ignore failing load balancer health checks on newly instantiated tasks to prevent premature shutdown, up to 2147483647. Only valid for services configured to use load balancers."
  default = 30
}

variable "launch_type" {
  type = string
  description = "(Optional) Type of cluster on which this service will be run, FARGATE or EC2, default is EC2"
  default = "EC2"
}

variable "scheduling_strategy" {
  type = string
  description = "(Optional) The scheduling strategy to use for the service. The valid values are REPLICA and DAEMON. Defaults to REPLICA"
  default = "REPLICA"
}

variable "deployment_maximum_percent" {
  type = number
  description = "Optional) The upper limit (as a percentage of the service's desiredCount) of the number of running tasks that can be running in a service during a deployment. Not valid when using the DAEMON scheduling strategy."
  default = 200
}

variable "deployment_minimum_healthy_percent" {
  type = number
  description = "(Optional) The lower limit (as a percentage of the service's desiredCount) of the number of running tasks that must remain running and healthy in a service during a deployment. "
  default = 100
}

variable "assign_public_ip" {
  type = bool
  description = "(Optional) Should the container isntance have a public IP adress"
  default = false
}

variable "additional_security_groups" {
  type = list(string)
  description = "(Optional) List of ids of additional SGs to which the service should belong"
  default = []
}

variable "vpc_id" {
  type = string
  description = "(Required) ID of VPC in which this service will be running"
}

variable "subnet_cidrs" {
  type = list(string)
  description = "(Required) List of CIDRs for subnets which this service will use"
}