# Consider moving this to base or account

# resource "aws_iam_role" "ecs_service_task_execution_role" {
#   name = "${local.resource_prefix}-execution_role"
#   assume_role_policy = data.aws_iam_policy_document.ecs_service_task_execution_role.json
# }

# data "aws_iam_policy_document" "ecs_service_task_execution_role" {
#   statement {
#     actions = ["sts:AssumeRole"]
#     principals {
#       type = "Service"
#       identifiers = ["ecs-tasks.amazonaws.com"]
#     }
#   }
# }

# resource "aws_iam_role_policy_attachment" "ecs_service_task_execution_role_attachment {
#   role = aws_iam_role.ecs_service_task_execution_role.name
#   policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
# }

data "aws_iam_role" "ecs_service_task_execution_role" {
  name = "BuildMHS-ECSTaskExecutionRole"
}