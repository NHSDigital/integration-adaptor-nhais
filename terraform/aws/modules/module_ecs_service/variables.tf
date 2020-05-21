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

variable "image_name" {
  type = string
  description = "Path to docker image to be used in task definition"
}

variable "environment_variables" {
  type = map(string)
  description = "Map of environment variables to pass to container"
  default = {}
}

variable "cpu_units" {
  type = number
  description = "Number of CPU units to assign to containers"
  default = 1024
}

variable "memory_units" {
  type = number
  description = "Number of Memory units to assign to containers"
  default = 2048
}

variable "network_mode" {
  type = string
  description = "Network mode for containers"
  default = "awsvpc"
}

variable "logs_retention" {
  type = number
  description = "Number of days to keep the logs in CloudWatch"
  default = 30
}

variable "log_stream_prefix" {
  type = string
  description = "Value for logs stream prefix"
}

variable "logs_datetime_format" {
  type = string
  description = "Format for date and time in logs"
  default = "\\[%Y-%m-%dT%H:%M:%S\\.%fZ\\]"
}

variable "load_balancer_type" {
  type = string
  description = "Type of loadbalancer for service, application or network"
  default = "application"
}

variable "protocol" {
  type = string
  description = "Protocol for load balancer, HTTP or HTTPS"
  default = "HTTP"
}