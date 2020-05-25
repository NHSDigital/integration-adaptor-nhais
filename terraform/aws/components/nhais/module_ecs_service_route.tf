module "nhais_ecs_service" {
  source = "../../modules/module_ecs_service"

  project         = var.project
  component       = var.component
  environment     = var.environment
  region          = var.region
  module_instance = "nhais_ecs"
  default_tags    = local.default_tags
  
  availability_zones = local.availability_zones

  image_name        = var.nhais_image_name
  cluster_id        = data.terraform_remote_state.base.outputs.base_cluster_id
  desired_count     = var.nhais_service_desired_count
  container_port    = var.nhais_service_container_port
  launch_type       = var.nhais_service_launch_type
  log_stream_prefix = var.build_id

  enable_load_balancing = false
  environment_variables = local.environment_variables
  task_execution_role_arn = aws_iam_role.ecs_service_task_execution_role.arn
  task_role_arn = data.aws_iam_role.ecs_service_task_role.arn
  
  additional_security_groups = [
    data.terraform_remote_state.base.outputs.core_sg_id,
    aws_security_group.docdb_access_sg.id
  ]

  vpc_id = data.terraform_remote_state.base.outputs.vpc_id
  subnet_ids = aws_subnet.service_subnet.*.id


}