module "nhais_ecs_service" {
  source = "../../modules/module_ecs_service"

  project         = var.project
  component       = var.component
  environment     = var.environment
  region          = var.region
  module_instance = "nhais_ecs"
  default_tags    = local.default_tags
  
  availability_zones = ["${var.region}a", "${var.region}b", "${var.region}c"]

  image_name        = var.nhais_image_name
  cluster_id        = data.terraform_remote_state.base.outputs.base_cluster_id
  desired_count     = var.nhais_service_desired_count
  container_port    = var.nhais_service_container_port
  launch_type       = var.nhais_service_launch_type
  log_stream_prefix = var.build_id

  enable_load_balancing = false
  environment_variables = local.environment_variables
  
  additional_security_groups = [data.terraform_remote_state.base.outputs.core_sg_id]
  vpc_id = data.terraform_remote_state.base.outputs.vpc_id
  subnet_cidrs = [
    cidrsubnet(data.terraform_remote_state.base.outputs.vpc_cidr,3,1),
    cidrsubnet(data.terraform_remote_state.base.outputs.vpc_cidr,3,2),
    cidrsubnet(data.terraform_remote_state.base.outputs.vpc_cidr,3,3)
  ]

}