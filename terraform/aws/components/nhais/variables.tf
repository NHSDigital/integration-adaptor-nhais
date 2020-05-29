variable "account_id" {
  type = string
  description = "ID of AWS Account on which the resources are created"
}

variable "project" {
  type = string
  description = "Name of the project where this code is used"
}

variable "environment" {
  type = string
  description = "Name of the environment"
}

variable "component" {
  type = string
  description = "Name of the component"
  default = "nhais"
}

variable "region" {
  type = string
  description = "Region where the resources will be created"
}

variable "base_cidr_block" {
  type = string
  description = "CIDR block to use for VPC"
}

variable "tf_state_bucket" {
  type = string
  description = "Name of S3 bucket with TF state of components"
}

variable "nhais_service_desired_count" {
  type = number
  description = "Number of containers to run in the service"
}

variable "nhais_service_container_port" {
  type = number
  description = "Port Number on which service within container will be listening"
}

variable "nhais_service_launch_type" {
  type = string
  description = "Type of cluster on which this service will be run, FARGATE or EC2"
}

# variable "nhais_image_name" {
#   type = string
#   description = "Path to docker image to be used in task definition"
# }

variable "build_id" {
  type = string
  description = "Number of the current build, used for tagging the logs"
}

variable "mq_broker_name" {
  type = string
  description = "Name of the MQ broker shared between all envs"
}

variable "environment_variables" {
  type = list(object({name=string, value=string}))
  description = "List of objects for Environment variables"
  default = []
}

variable "docdb_master_user" {
  type = string
  description = "Username for Document DB master user"
  default = "master_user"
}

variable "docdb_master_password" {
  type = string
  description = "Password for Document DB master user"
  default = "ChangeMe"
}

variable "nhais_log_level" {
  type = string
  description = "Level of logging for NHAIS application"
  default = "INFO"
}

variable "nhais_db_parameters" {
  type = list(object({name=string, value=string}))
  description = "List of parameters for DocDB"
  default = [
    {
      name = "tls"
      value = "disabled"
    },
    { 
      name = "audit_logs"
      value = "enabled"
    }
  ]
}
